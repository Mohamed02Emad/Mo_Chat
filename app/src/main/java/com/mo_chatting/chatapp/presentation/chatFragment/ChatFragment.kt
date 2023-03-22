package com.mo_chatting.chatapp.presentation.chatFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    private lateinit var binding:FragmentChatBinding
    private val args:ChatFragmentArgs by navArgs()
    private lateinit var thisRoom:Room
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        thisRoom=args.room
        binding= FragmentChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
        setOnClicks()
    }

    private fun setOnClicks() {
        binding.apply {
            btnBackArrow.setOnClickListener {
                findNavController().navigateUp()
            }

            btnSend.setOnClickListener {
                showToast("Soon")
            }

            btnRoomInfo.setOnClickListener {
                showToast("Soon")
            }
        }
    }

    private fun setViews() {
        binding.tvRoomName.text=thisRoom.roomName
    }

    private fun showToast(string: String){
        Toast.makeText(requireContext(),string, Toast.LENGTH_LONG).show()
    }
}