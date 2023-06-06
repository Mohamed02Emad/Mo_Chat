package com.mo_chatting.chatapp.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mo_chatting.chatapp.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by viewModels()
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
            setOnClicks()
        }
    }

    private fun setOnClicks() {
        binding.apply {
            btnBackArrow.setOnClickListener {
                findNavController().navigateUp()
            }

            btnDarkMode.setOnClickListener {
                binding.swDarkMode.isChecked = !binding.swDarkMode.isChecked
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.setDarkMode()
                    changeDarkMode(viewModel.darkModeSwitch.value!!)
                }
            }

            btnImgQuality.setOnClickListener {
                binding.swImgQuality.isChecked = !binding.swImgQuality.isChecked
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.setLowImageQuality()
                }
            }


            btnNotifications.setOnClickListener {
                binding.swNotifications.isChecked = !binding.swNotifications.isChecked
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.setNotificationEnabled()
                }
            }

        }
    }

    private fun changeDarkMode(it: Boolean) {
        try {
            CoroutineScope(Dispatchers.Main).launch {
                if (it) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(requireActivity(), e.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private suspend fun setViews() {
        binding.apply {
            swDarkMode.isChecked = viewModel.getDarkMode()
            swImgQuality.isChecked = viewModel.getLowImageQuality()
            swNotifications.isChecked = viewModel.getNotificationEnabled()
        }
    }


}