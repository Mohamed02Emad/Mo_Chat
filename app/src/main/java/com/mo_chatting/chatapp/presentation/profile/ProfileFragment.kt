package com.mo_chatting.chatapp.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.mo_chatting.chatapp.MyFragmentParent
import com.mo_chatting.chatapp.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ProfileFragment : MyFragmentParent() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        while (viewModel.firebaseAuth.currentUser == null) {
        }
        lifecycleScope.launch {
            setUserViews()
            //setOnClicks()
        }
    }

    private suspend fun setUserViews() {
        val name = viewModel.getUserName()
        if (name != "null" || name.isBlank()) {
            withContext(Dispatchers.Main) {
                binding.etUserName.setText(name)
            }
        } else {
            viewModel.setUserName()
            setUserViews()
            return
        }
        withContext(Dispatchers.Main) {
            val img = viewModel.getUserImageFromDataStore()
            val uri = if (img != null) {
                img
            } else {
                //todo : need improves
                viewModel.setUserImageAtDataStore()
                viewModel.getUserImageFromDataStore()
            }
            try {
                Glide.with(requireContext())
                    .load(uri)
                    .override(500, 400)
                    .into(binding.ivProfileImage)
            } catch (e: Exception) {
                showToast(e.message.toString())
            }
        }
    }




}