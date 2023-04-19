package com.mo_chatting.chatapp.presentation.MainActivity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.data.repositories.RoomsRepository
import com.mo_chatting.chatapp.databinding.ActivityMainBinding
import com.mo_chatting.chatapp.presentation.groupChat.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var repository: RoomsRepository

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigation()
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

    private fun setupNavigation() {
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.navController)
        setUpVisibilityOfBottomBar()
    }

    private fun setUpVisibilityOfBottomBar() {
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.homeFragment, R.id.directChats, R.id.profileFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                }
                else -> {
                    bottomNavigationView.visibility = View.GONE
                }
            }
        }
    }

    private suspend fun getRoomByRoomId(roomIdFromNotification: String?): Room? {
        return repository.getRoomById(roomIdFromNotification!!)
    }
}