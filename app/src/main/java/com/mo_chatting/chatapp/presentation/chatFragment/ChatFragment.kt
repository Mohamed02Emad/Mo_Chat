package com.mo_chatting.chatapp.presentation.chatFragment

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
import com.mo_chatting.chatapp.data.models.Message
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.databinding.FragmentChatBinding
import com.mo_chatting.chatapp.presentation.recyclerViews.ChatAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {

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
        setOnClicks()
        setupRecyclerView()
        setObservers()
    }

    private fun setObservers() {
        viewModel.messageList.observe(viewLifecycleOwner) {
            try {
                refreshRV()
            } catch (_: Exception) {
            }
        }
    }

    private fun refreshRV() {
        binding.rvChat.adapter!!.notifyDataSetChanged()
    }

    private fun setOnClicks() {
        binding.apply {
            btnBackArrow.setOnClickListener {
                findNavController().navigateUp()
            }

            btnSend.setOnClickListener {
                if (binding.etMessage.text.isNullOrBlank()) return@setOnClickListener
                viewModel.addFakeMessage(
                    Message(
                        viewModel.getUserId(),
                        binding.etMessage.text.toString(),
                        "23/3/2002 17:40",
                        false
                    )
                )
                binding.etMessage.setText("")
            }

            btnRoomInfo.setOnClickListener {
                showToast("Soon")
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

    private fun onChatLongClick(room: Message, position: Int) {

    }

    private fun onChatClick(message: Message, position: Int) {

    }

    private fun setViews() {
        binding.tvRoomName.text = thisRoom.roomName
    }

    private fun showToast(string: String) {
        Toast.makeText(requireContext(), string, Toast.LENGTH_LONG).show()
    }
}