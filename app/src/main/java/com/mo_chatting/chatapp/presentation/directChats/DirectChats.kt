package com.mo_chatting.chatapp.presentation.directChats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mo_chatting.chatapp.appClasses.mapDirectChatToRoom
import com.mo_chatting.chatapp.data.models.DirectContact
import com.mo_chatting.chatapp.databinding.FragmentDirectChatsBinding
import com.mo_chatting.chatapp.presentation.dialogs.searchUser.SearchUserDialog
import com.mo_chatting.chatapp.presentation.recyclerViews.DirectChatsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DirectChats : Fragment() {

    private lateinit var binding: FragmentDirectChatsBinding
    private val viewModel: DirectChatViewModel by viewModels()
    private lateinit var adapter: DirectChatsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDirectChatsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            setOnClicks()
            setObservers()
            viewModel.getCachedChats("token")
        }
    }

    private fun setObservers() {
        viewModel.chats.observe(viewLifecycleOwner) { chats ->
            lifecycleScope.launch {
                if (chats.size > 0) {
                    setUpRecyclerView(chats)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.chatsFlow.collect() {
                viewModel.addNewChatsFromFireBaseToChatList(it)
            }
        }
    }

    private suspend fun setUpRecyclerView(list: ArrayList<DirectContact>?) {
        adapter = DirectChatsAdapter(
            list!!,
            DirectChatsAdapter.OnChatClickListener(
                { chat, position ->
                    lifecycleScope.launch {
                        chatClicked(chat)
                    }
                },
                { chat, position ->
                    //chat long click
                    false
                },
                { chat, position ->
                    // delete chat
                },
                { chat, position ->
                    // edit chat
                },
                { chat, position ->
                    //pin chat
                }),
            viewModel.getUserName()
        )
        binding.rvDirectChats.adapter = adapter
        binding.rvDirectChats.layoutManager = LinearLayoutManager(requireActivity())

    }

    private suspend fun chatClicked(chat: DirectContact) {
       val room = mapDirectChatToRoom(chat , viewModel.getUserName())
        withContext(Dispatchers.Main){
            findNavController().navigate(DirectChatsDirections.actionDirectChatsToChatFragment(room))
        }
    }

    private fun setOnClicks() {
        binding.btnSettings.setOnClickListener {
            settingsClicked()

        }

        binding.fabAdd.setOnClickListener {
            val searchField = SearchUserDialog()
            searchField.show(requireActivity().supportFragmentManager,null)
        }
    }

    private fun settingsClicked() {
        findNavController().navigate(DirectChatsDirections.actionDirectChatsToSettingsFragment())
    }
}