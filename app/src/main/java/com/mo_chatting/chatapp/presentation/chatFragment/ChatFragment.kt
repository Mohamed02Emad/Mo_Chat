package com.mo_chatting.chatapp.presentation.chatFragment


import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.appClasses.Constants
import com.mo_chatting.chatapp.data.models.Message
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.databinding.FragmentChatBinding
import com.mo_chatting.chatapp.presentation.dialogs.RoomIdDialog
import com.mo_chatting.chatapp.presentation.dialogs.RoomUsersDialog
import com.mo_chatting.chatapp.presentation.dialogs.UserImageDialog
import com.mo_chatting.chatapp.presentation.recyclerViews.ChatAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {

    @Inject
    lateinit var firebaseStore: FirebaseFirestore

    private lateinit var binding: FragmentChatBinding
    private val args: ChatFragmentArgs by navArgs()
    private lateinit var thisRoom: Room
    private lateinit var adapter: ChatAdapter
    private val viewModel: ChatFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        thisRoom = args.room
        binding = FragmentChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getInitialData(thisRoom)
            withContext(Dispatchers.Main) {
                setupRecyclerView()
                setOnClicks()
            }
            binding.rvChat.scrollToPosition(binding.rvChat.adapter!!.itemCount - 1)
        }
    }

    private fun setOnClicks() {
        binding.apply {
            btnBackArrow.setOnClickListener {
                findNavController().navigateUp()
            }

            btnSend.setOnClickListener {
                if (binding.etMessage.text.isNullOrBlank()) return@setOnClickListener
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main) {
                        binding.btnSend.isClickable = false
                    }
                    viewModel.sendMessage(
                        Message(
                            viewModel.getUserId(),
                            binding.etMessage.text.toString().trimEnd(),
                            messageDateAndTime = viewModel.getDate(),
                            messageOwner = viewModel.getUserName(),
                            viewModel.firebaseAuth.currentUser!!.displayName.toString(),
                            timeWithMillis = System.currentTimeMillis().toString()
                        ), room = thisRoom
                    )
                    withContext(Dispatchers.Main) {
                        binding.btnSend.isClickable = true
                        binding.etMessage.setText("")
                        // scrollRV()
                    }
                }
            }

            pushViewsToTopOfKeyBoard()

            btnRoomInfo.setOnClickListener {
                showMenu(it!!)
            }

            clipCard.setOnClickListener {
                showSendOptions(it)
            }
            binding.root.setOnClickListener {
                if (binding.attachMenuFrame.isVisible) {
                    binding.attachMenuFrame.visibility = View.GONE
                }
            }
        }


        firebaseStore.collection("${Constants.roomsChatCollection}${thisRoom.roomId}")
            .addSnapshotListener { value, error ->
                error?.let {
                    return@addSnapshotListener
                }
                value?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.resetList(it,thisRoom)
                        withContext(Dispatchers.Main) {
                            binding.rvChat.adapter!!.notifyDataSetChanged()
                            smoothRefreshRV()
                        }
                    }
                }
            }
    }

    private fun showSendOptions(view: View) {
        if (binding.attachMenuFrame.isVisible){
            binding.attachMenuFrame.visibility=View.GONE
        }else{
            binding.attachMenuFrame.visibility=View.VISIBLE
        }
    }


    private fun setupRecyclerView() {
        adapter = ChatAdapter(
            viewModel.messageList.value!!,
            ChatAdapter.OnChatClickListener({ message, position ->
                onChatClick(message, position)
            }, { message, position ->
                onChatLongClick(message, position)
                false
            }, { userId, userName ->
                messageUserNameClicked(userId, userName)
            }), viewModel.getUserId()
        )
        binding.rvChat.adapter = adapter
        binding.rvChat.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun messageUserNameClicked(userId: String, userName: String) {
        val userImageDialog = UserImageDialog(userId, userName)
        userImageDialog.show(requireActivity().supportFragmentManager, null)
    }

    private fun pushViewsToTopOfKeyBoard() {
        val rootView = binding.root

        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val screenHeight = resources.displayMetrics.heightPixels
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val keyboardHeight = screenHeight - rect.bottom
            if (keyboardHeight > screenHeight * 0.15) {
                if (viewModel.isKeyboard) {

                } else {
                    viewModel.isKeyboard = true
                    smoothRefreshRV()
                }
            } else {
                viewModel.isKeyboard = false
            }

        }
    }

    private fun onChatLongClick(message: Message, position: Int) {

    }

    private fun onChatClick(message: Message, position: Int) {

    }

    private fun setViews() {
        binding.tvRoomName.text = thisRoom.roomName
        setBackground(thisRoom.roomBackgroundColor)

    }

    private fun smoothRefreshRV() {
        try {
            val lastPosition = binding.rvChat.adapter?.itemCount?.minus(1) ?: 0
            binding.rvChat.smoothScrollToPosition(lastPosition)
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    private fun scrollRV() {
        try {
            val lastPosition = binding.rvChat.adapter?.itemCount?.minus(1) ?: 0
            binding.rvChat.scrollToPosition(lastPosition)
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    private fun showToast(string: String) {
        Toast.makeText(requireContext(), string, Toast.LENGTH_LONG).show()
    }

    fun showMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.room_option_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.room_id -> {
                    val roomIdDialog = RoomIdDialog(thisRoom.roomId)
                    roomIdDialog.show(requireActivity().supportFragmentManager, null)
                    true
                }
                R.id.change_background -> {
                    var x = thisRoom.roomBackgroundColor
                    x++
                    if (x > 7) x = 0
                    setBackground(x)
                    thisRoom.roomBackgroundColor = x
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.updateRoomBackground(thisRoom)
                    }
                    true
                }
                R.id.show_room_members -> {
                    showRoomMembers()
                    true
                }

                else -> false
            }
        }
        popup.show()
    }

    private fun showRoomMembers() {
        val usersDialog = RoomUsersDialog(thisRoom)
        usersDialog.show(requireActivity().supportFragmentManager,null)
    }

    private fun setBackground(background: Int) {
        when (background) {
            0 -> {
                binding.backgroundImg.setImageResource(R.color.black)
            }
            1 -> {
                binding.backgroundImg.setImageResource(R.color.blue_white)
            }
            2 -> {
                binding.backgroundImg.setImageResource(R.color.grey)
            }
            3 -> {
                binding.backgroundImg.setImageResource(R.color.dark_yellow)
            }
            4 -> {
                binding.backgroundImg.setImageResource(R.color.red_background)
            }
            5 -> {
                binding.backgroundImg.setImageResource(R.color.blue_50)
            }
            6 -> {
                binding.backgroundImg.setImageResource(R.color.card_blue)
            }
            7 -> {
                binding.backgroundImg.setImageResource(R.color.light_blue)
            }
        }

    }
}