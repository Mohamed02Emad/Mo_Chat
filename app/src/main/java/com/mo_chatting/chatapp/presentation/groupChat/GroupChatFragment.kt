package com.mo_chatting.chatapp.presentation.groupChat

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mo_chatting.chatapp.MyFragmentParent
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.appClasses.Constants
import com.mo_chatting.chatapp.appClasses.isInternetAvailable
import com.mo_chatting.chatapp.appClasses.swipeToDelete.SwipeToDeleteCallback
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.databinding.FragmentHomeBinding
import com.mo_chatting.chatapp.presentation.dialogs.AddRoomDialog
import com.mo_chatting.chatapp.presentation.dialogs.CreateRoomDialog
import com.mo_chatting.chatapp.presentation.dialogs.DialogsInterface
import com.mo_chatting.chatapp.presentation.dialogs.EnterPasswordDialog
import com.mo_chatting.chatapp.presentation.recyclerViews.HomeRoomAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class GroupChatFragment : MyFragmentParent(), DialogsInterface {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firebaseStore: FirebaseFirestore

    private lateinit var adapter: HomeRoomAdapter
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: GroupChatViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            while (viewModel.firebaseAuth.currentUser == null) {

            }
            viewModel.setupUserId()
            viewModel.setConstantUid()
            setOnClicks()
            setupRecyclerView()
            oservers()
            setupSwipeToDelete()
        }
    }


    private fun setOnClicks() {

        binding.fabAdd.setOnClickListener {
            if (isInternetAvailable(requireContext())) {
                showAddRoomDialog()
            } else {
                showToast("No Internet")
            }
        }

        binding.btnSettings.setOnClickListener {
            settingsClicked()
        }
    }

    private fun settingsClicked() {
        findNavController().navigate(GroupChatFragmentDirections.actionHomeFragmentToSettingsFragment())
    }

    private fun oservers() {
        viewModel.roomsList.observe(viewLifecycleOwner) {
            try {
                setupRecyclerView()
            } catch (_: Exception) {
            }
        }
        lifecycleScope.launch {
            viewModel.roomsFlow.collect() {
                viewModel.addNewRoomsFromFireBaseToRoomList(it)
            }
        }
    }

    private fun setupRecyclerView() {

        adapter = HomeRoomAdapter(
            viewModel.roomsList.value!!,
            viewModel.firebaseAuth.currentUser!!.uid,
            HomeRoomAdapter.OnRoomClickListener({ room, position ->
                onRoomClick(room, position)
            }, { room, position ->
                onRoomLongClick(room, position)
                false
            }, { room, position ->
                // not used
                deleteRoom(room, position)
            }, { room, position ->
                // not used
                editRoom(room, position)
            }, { room, position ->
                // not used
                pinRoom(room, position)
            }
            )
        )
        binding.rvHome.adapter = adapter
        binding.rvHome.layoutManager = LinearLayoutManager(requireActivity())

    }

    private fun editRoom(room: Room, position: Int) {
        showEditRoomDialog(room, position)
    }

    private fun showEditRoomDialog(room: Room, position: Int) {
        if (!isInternetAvailable(requireContext())) {
            showToast("No Internet")
            return
        }
        val editRoomDialog = CreateRoomDialog(this, true, room)
        editRoomDialog.show(requireActivity().supportFragmentManager, null)
    }

    private fun deleteRoom(room: Room, position: Int) {
        if (!isInternetAvailable(requireContext())) {
            showToast("No Internet")
            return
        }
        val builder = AlertDialog.Builder(context, R.style.MyDialog)
        builder.setTitle("Delete this item?")
        builder.setPositiveButton("Yes") { dialog, which ->
            lifecycleScope.launch {
                viewModel.deleteRoom(room)
                withContext(Dispatchers.Main) {
                    binding.rvHome.adapter!!.notifyItemRemoved(position)
                    val snackbar = Snackbar
                        .make(
                            binding.rvHome,
                            "Removed",
                            Snackbar.LENGTH_SHORT
                        )
                    snackbar.show()
                    dialog.dismiss()
                }
            }
        }
        builder.setNegativeButton("No") { dialog, which ->
            adapter.notifyItemChanged(position)
            dialog.dismiss()
        }
            .setCancelable(false)
        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.main_text))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.main_text))
        }
        dialog.show()
    }

    private fun onRoomLongClick(room: Room, position: Int) {
        showToast(room.roomName)
    }

    private fun onRoomClick(room: Room, position: Int) {
        findNavController().navigate(
            GroupChatFragmentDirections.actionHomeFragmentToChatFragment(
                room
            )
        )
    }

    private fun pinRoom(room: Room, position: Int) {
        showToast("not yet")
    }

    private fun showAddRoomDialog() {
        if (isInternetAvailable(requireContext())) {
            val addRoomDialog = AddRoomDialog(this)
            addRoomDialog.show(requireActivity().supportFragmentManager, null)
        } else {
            showToast("No Internet")
        }
    }

    override fun onDataPassedCreateRoom(room: Room) {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.createNewRoom(room)
        }
    }

    override fun onRoomEditPassed(room: Room) {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.updateRoom(room)
        }
    }

    override fun onDataPassedJoinRoom(roomId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val room = viewModel.checkIfRoomExist(roomId)
            if (room != null) {
                if (room.hasPassword) {
                    val enterPasswordDialog = EnterPasswordDialog(this@GroupChatFragment, room)
                    enterPasswordDialog.show(requireActivity().supportFragmentManager, null)
                } else {
                    viewModel.joinRoom(room)
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "No Room with that id", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    override fun onPasswordReceive(room: Room) {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.joinRoom(room)
        }
    }

    private fun setupSwipeToDelete() {
        val swipeToDeleteCallback: SwipeToDeleteCallback =
            object : SwipeToDeleteCallback(requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                    removeAfterSwiped(viewHolder)
                }
            }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvHome)
    }

    private fun removeAfterSwiped(viewHolder: RecyclerView.ViewHolder) {
        deleteRoom(
            adapter.getItemByPosition(viewHolder.adapterPosition),
            viewHolder.adapterPosition
        )
    }

}