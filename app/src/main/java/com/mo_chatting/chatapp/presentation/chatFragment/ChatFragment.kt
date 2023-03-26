package com.mo_chatting.chatapp.presentation.chatFragment


import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.mo_chatting.chatapp.appClasses.Constants
import com.mo_chatting.chatapp.data.models.Message
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.databinding.FragmentChatBinding
import com.mo_chatting.chatapp.presentation.dialogs.RoomIdDialog
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
                            viewModel.firebaseAuth.currentUser!!.displayName.toString()
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
                val roomIdDialog = RoomIdDialog(thisRoom.roomId)
                roomIdDialog.show(requireActivity().supportFragmentManager, null)
            }
        }

        firebaseStore.collection("${Constants.roomsChatCollection}${thisRoom.roomId}")
            .addSnapshotListener { value, error ->
                error?.let {
                    return@addSnapshotListener
                }
                value?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.resetList(it)
                        withContext(Dispatchers.Main) {
                            binding.rvChat.adapter!!.notifyDataSetChanged()
                            scrollRV()
                        }
                    }
                }
            }
    }


    private fun setupRecyclerView() {
        adapter = ChatAdapter(
            viewModel.messageList.value!!,
            ChatAdapter.OnChatClickListener { message, position ->
                onChatClick(message, position)
            },
            ChatAdapter.OnChatLongClickListener { message, position ->
                onChatLongClick(message, position)
                false
            }, viewModel.getUserId()
        )
        binding.rvChat.adapter = adapter
        binding.rvChat.layoutManager = LinearLayoutManager(requireActivity())
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
                    //scrollRV()
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
    }

    private fun smoothRefreshRV() {
        try {
            binding.rvChat.adapter!!.notifyItemInserted(viewModel.messageList.value!!.size)
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
}