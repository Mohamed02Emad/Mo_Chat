package com.mo_chatting.chatapp.presentation.home

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mo_chatting.chatapp.AuthActivity
import com.mo_chatting.chatapp.MyFragmentParent
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.appClasses.Constants
import com.mo_chatting.chatapp.appClasses.Constants.roomsCollection
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.databinding.FragmentHomeBinding
import com.mo_chatting.chatapp.presentation.dialogs.RenameDialog
import com.mo_chatting.chatapp.presentation.recyclerViews.HomeRoomAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : MyFragmentParent(){

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    @Inject
    lateinit var firebaseStore : FirebaseFirestore

    private lateinit var adapter: HomeRoomAdapter
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch {
            setUserViews()
        }
        setOnClicks()
        setupRecyclerView()
        oservers()
    }

    private suspend fun setUserViews() {
        val uri = viewModel.getUserImage()
        val currentUser = firebaseAuth.currentUser
        val currentUserName = currentUser?.displayName.toString()
        if (currentUserName == "null") {
              restart()
        } else {
            binding.tvUserName.text = currentUserName
        }
        withContext(Dispatchers.Main) {
            Glide.with(requireContext())
                .load(uri)
                .error(R.drawable.ic_profile)
                .override(500, 400)
                .into(binding.profile)
        }
    }


    private fun oservers() {
        viewModel.roomsList.observe(viewLifecycleOwner) {
            try {
                setupRecyclerView()
            } catch (_: Exception) {
            }
        }
    }

    private fun refreshRV(){
     //   binding.rvHome.adapter!!.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        adapter = HomeRoomAdapter(viewModel.roomsList.value!!,
            HomeRoomAdapter.OnRoomClickListener { room, position ->
                onRoomClick(room, position)
            },
            HomeRoomAdapter.OnLongClickListener { room, position ->
                onRoomLongClick(room, position)
                false
            }
        )
        binding.rvHome.adapter = adapter
        binding.rvHome.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun onRoomLongClick(room: Room, position: Int) {
        showToast(room.roomName)
    }

    private fun onRoomClick(room: Room, position: Int) {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToChatFragment(room))
    }

    private fun setOnClicks() {
        binding.btnLogout.setOnClickListener {
            viewModel.signOut()
            startActivity(Intent(requireActivity(), AuthActivity::class.java))
            requireActivity().finish()
        }

        binding.fabAdd.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.createNewRoom(Room("testRoom", false, 0, "123", "mohamed"))
            }
        }

        binding.tvEditImage.setOnClickListener {
            showBottomSheet()
        }

        binding.btnEditName.setOnClickListener {
            showNameDialog()
        }

        //firebase listener
        firebaseStore.collection(roomsCollection).addSnapshotListener { value, error ->

            error?.let {
                return@addSnapshotListener
            }
            value?.let {
                viewModel.resetList(value)
            }

        }

    }

    private fun showNameDialog() {
        val dialogFragment = RenameDialog()
        dialogFragment.show(requireActivity().supportFragmentManager, null)
    }

    private fun showBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.chose_edit_image, null)
        val btnCamera: LinearLayout = view.findViewById(R.id.camera_choice)
        btnCamera.setOnClickListener {
            startCameraIntent()
            dialog.dismiss()
        }
        val btnGallery: LinearLayout = view.findViewById(R.id.gallery_choice)
        btnGallery.setOnClickListener {
            startGalleryIntent()
            dialog.dismiss()
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun startCameraIntent() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        viewModel.uri.value = requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, viewModel.uri.value)

        cameraResultLauncher.launch(cameraIntent)
    }

    private fun startGalleryIntent() {
        val i = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        galleryResultLauncher.launch(i)
    }

    private val galleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                viewModel.uri.value = data!!.data
                binding.profile.setImageURI(viewModel.uri.value)
                viewModel.updateUserData()
            }
        }

    private val cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (viewModel.uri.value != null)
                    binding.profile.setImageURI(viewModel.uri.value)
                viewModel.updateUserData()
            }
        }


}