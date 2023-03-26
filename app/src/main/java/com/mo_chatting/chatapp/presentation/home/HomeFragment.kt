package com.mo_chatting.chatapp.presentation.home

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
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
import com.mo_chatting.chatapp.appClasses.Constants.roomsCollection
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.databinding.FragmentHomeBinding
import com.mo_chatting.chatapp.presentation.dialogs.*
import com.mo_chatting.chatapp.presentation.recyclerViews.HomeRoomAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : MyFragmentParent(), MyDialogListener, MyRenameDialogListener,
    MyJoinRoomListener, MyEnterPasswordListener {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firebaseStore: FirebaseFirestore

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

        val name = viewModel.getUserName()
        if (name != "null") {
            withContext(Dispatchers.Main) {
                binding.tvUserName.text = name
            }
        } else {

            viewModel.setUserName()
            withContext(Dispatchers.Main) {
                binding.tvUserName.text = viewModel.getUserName()
            }
        }

        val img = viewModel.getUserImageFromDataStore()
        val uri = Uri.parse(
            if (img != "null") {
                img
            } else {
                viewModel.setUserImageAtDataStore()
                viewModel.getUserImageFromDataStore()

            }
        )

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
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.signOut()
                withContext(Dispatchers.Main) {
                    startActivity(Intent(requireActivity(), AuthActivity::class.java))
                    requireActivity().finish()
                }
            }
        }

        binding.fabAdd.setOnClickListener {
            showAddRoomDialog()
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

    private fun showAddRoomDialog() {
        val addRoomDialog = AddRoomDialog(this)
        addRoomDialog.show(requireActivity().supportFragmentManager, null)
    }

    private fun showNameDialog() {
        val dialogFragment = RenameDialog(this)
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

    override fun onDataPassed(room: Room) {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.createNewRoom(room)
        }
    }

    override fun onDataPassedRename(name: String) {
        binding.tvUserName.text = name
    }

    override fun onDataPassedJoinRoom(roomId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val room = viewModel.checkIfRoomExist(roomId)
            if (room != null) {
                if (room.hasPassword) {
                    val enterPasswordDialog = EnterPasswordDialog(this@HomeFragment, room)
                    enterPasswordDialog.show(requireActivity().supportFragmentManager, null)
                } else {
                    viewModel.joinRoom(room)
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "No Room with that id", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    override fun onPasswordReceive(room: Room) {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.joinRoom(room)
        }
    }

}