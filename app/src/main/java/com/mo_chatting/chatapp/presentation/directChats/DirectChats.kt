package com.mo_chatting.chatapp.presentation.directChats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mo_chatting.chatapp.databinding.FragmentDirectChatsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DirectChats : Fragment() {

    private lateinit var binding: FragmentDirectChatsBinding
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
    }

    private fun setOnClicks() {
        binding.btnSettings.setOnClickListener {
            settingsClicked()
        }
    }

    private fun settingsClicked() {
        findNavController().navigate(DirectChatsDirections.actionDirectChatsToSettingsFragment())
    }
}