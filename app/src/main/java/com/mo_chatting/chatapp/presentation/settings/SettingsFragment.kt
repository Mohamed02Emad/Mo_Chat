package com.mo_chatting.chatapp.presentation.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding:FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
        setOnClicks()
        setObservers()
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
           }

           btnImgQuality.setOnClickListener {
               binding.swImgQuality.isChecked=!binding.swImgQuality.isChecked
           }
       }
    }

    private fun setViews() {
       binding.apply {

       }
    }


}