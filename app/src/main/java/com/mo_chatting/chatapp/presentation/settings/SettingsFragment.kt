package com.mo_chatting.chatapp.presentation.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding:FragmentSettingsBinding
    private val viewModel : SettingsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            setViews()
        }
        setOnClicks()
    }

    private fun setObservers() {
        //TODO("Not yet implemented")
    }

    private fun setOnClicks() {
       binding.apply {
           btnBackArrow.setOnClickListener {
               findNavController().navigateUp()
           }

           btnDarkMode.setOnClickListener {
               binding.swDarkMode.isChecked=!binding.swDarkMode.isChecked
               CoroutineScope(Dispatchers.IO).launch {
                   viewModel.setDarkMode()
               }
           }

           btnImgQuality.setOnClickListener {
               binding.swImgQuality.isChecked=!binding.swImgQuality.isChecked
               CoroutineScope(Dispatchers.IO).launch {
                   viewModel.setLowImageQuality()
               }
           }


           btnNotifications.setOnClickListener {
               binding.swNotifications.isChecked=!binding.swNotifications.isChecked
               CoroutineScope(Dispatchers.IO).launch {
                   viewModel.setNotificationEnabled()
               }
           }



       }
    }

    private suspend fun setViews() {
       binding.apply {
           swDarkMode.isChecked=viewModel.getDarkMode()
           swImgQuality.isChecked = viewModel.getLowImageQuality()
           swNotifications.isChecked=viewModel.getNotificationEnabled()
       }
    }


}