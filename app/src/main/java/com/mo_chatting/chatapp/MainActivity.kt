package com.mo_chatting.chatapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.data.repositories.RoomsRepository
import com.mo_chatting.chatapp.databinding.ActivityMainBinding
import com.mo_chatting.chatapp.presentation.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var repository: RoomsRepository

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch(Dispatchers.IO) {
            val roomIdFromNotification = intent.getStringExtra("roomId") ?: return@launch
            val room = getRoomByRoomId(roomIdFromNotification) ?: return@launch
            try {
                binding.navHostFragment.findNavController()
                    .navigate(HomeFragmentDirections.actionHomeFragmentToChatFragment(room))
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
             //       Toast.makeText(this@MainActivity, e.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun getRoomByRoomId(roomIdFromNotification: String?): Room? {
        return repository.getRoomById(roomIdFromNotification!!)
    }
}