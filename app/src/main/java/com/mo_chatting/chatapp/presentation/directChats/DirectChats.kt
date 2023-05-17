package com.mo_chatting.chatapp.presentation.directChats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mo_chatting.chatapp.data.models.DirectContact
import com.mo_chatting.chatapp.databinding.FragmentDirectChatsBinding
import com.mo_chatting.chatapp.presentation.dialogs.searchUser.SearchUserDialog
import com.mo_chatting.chatapp.presentation.recyclerViews.DirectChatsAdapter
import dagger.hilt.android.AndroidEntryPoint

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
        setOnClicks()
        setObservers()
        viewModel.getCachedChats("token")
    }

    private fun setObservers() {
        viewModel.chats.observe(viewLifecycleOwner) { chats ->
            if (chats.size > 0) {
                setUpRecyclerView(chats)
            }

        }
    }

    private fun setUpRecyclerView(list: ArrayList<DirectContact>?) {
        adapter = DirectChatsAdapter(
            list!!,
            DirectChatsAdapter.OnChatClickListener(
                { chat, position ->
                    //chat clicked
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
                })
        )
        binding.rvDirectChats.adapter = adapter
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