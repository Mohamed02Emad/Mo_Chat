package com.mo_chatting.chatapp.presentation.MainActivity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.appClasses.Constants.isOnline
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

    private val pushPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        isOnline = true
        setContentView(binding.root)
        lifecycleScope.launch {
            requestForPermission()
            setupNavigation()
            navigateToFragmentFromNotifications()
        }
    }

    private suspend fun navigateToFragmentFromNotifications() {
            val roomIdFromNotification = intent.getStringExtra("roomId") ?: return
            val room = getRoomByRoomId(roomIdFromNotification) ?: return
            try {
                navHostFragment.navController.navigate(HomeFragmentDirections.actionHomeFragmentToChatFragment(room))

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
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
                R.id.homeFragment, R.id.directChats, R.id.profileFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                }
                else -> {
                    bottomNavigationView.visibility = View.GONE
                }
            }
        }
    }

    private fun requestForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
                pushPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

    }

    private suspend fun getRoomByRoomId(roomIdFromNotification: String?): Room? {
        return repository.getRoomById(roomIdFromNotification!!)
    }

    override fun onPause() {
        super.onPause()
        isOnline = false
    }

    override fun onResume() {
        super.onResume()
        isOnline =true
    }

}