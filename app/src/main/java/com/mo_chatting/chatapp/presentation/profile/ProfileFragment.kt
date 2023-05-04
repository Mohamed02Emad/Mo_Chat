package com.mo_chatting.chatapp.presentation.profile

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mo_chatting.chatapp.AuthActivity
import com.mo_chatting.chatapp.MyFragmentParent
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.appClasses.isInternetAvailable
import com.mo_chatting.chatapp.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

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
            setOnClicks()
        }
    }

    private fun setOnClicks() {
        binding.tvLogout.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.signOut()
                withContext(Dispatchers.Main) {
                    startActivity(Intent(requireActivity(), AuthActivity::class.java))
                    requireActivity().finish()
                }
            }
        }

        binding.btnSave.apply {
            setOnClickListener {
                startAnimation {
                    binding.progressBar.visibility = View.VISIBLE
                    it.isClickable = false
                    lifecycleScope.launch(Dispatchers.Main){
                        delay(400)
                        revertAnimation()
                        it.isClickable = true
                        binding.progressBar.visibility = View.GONE
                        delay(130)
                        it.visibility = View.GONE
                    }
                    if (viewModel.userImageChanged && viewModel.uri.value != null) {
                        viewModel.updateUserImage()
                    }
                    lifecycleScope.launch(Dispatchers.Main) {
                        if (viewModel.getUserName() != binding.etUserName.text.toString()) {
                            viewModel.updateUserName(binding.etUserName.text.toString())
                        }
                    }

                    if (!isInternetAvailable(requireContext())) {
                        Toast.makeText(
                            requireContext(),
                            "no internet , your data might not get updated",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        binding.ivProfileImage.setOnClickListener {
            showProfileImageOptions()
        }

        binding.etUserName.doAfterTextChanged {
            lifecycleScope.launch {
                if (it.toString() == viewModel.getUserName() && !viewModel.userImageChanged){
                    binding.btnSave.visibility=View.GONE
                }else{
                    binding.btnSave.visibility=View.VISIBLE
                }
            }
        }
    }

    private fun showProfileImageOptions() {
        if (!isInternetAvailable(requireContext())) {
            showToast("No Internet")
            return
        }
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.profile_image_options, null)
        val btnShow: LinearLayout = view.findViewById(R.id.show_image)
        btnShow.setOnClickListener {
            showUserImage()
            dialog.dismiss()
        }
        val btnChange: LinearLayout = view.findViewById(R.id.change_image)
        btnChange.setOnClickListener {
            editUserImage()
            dialog.dismiss()
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun editUserImage() {
        if (!isInternetAvailable(requireContext())) {
            showToast("No Internet")
            return
        }
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.chose_edit_image, null)
        val btnCamera: LinearLayout = view.findViewById(R.id.camera_choice)
        btnCamera.setOnClickListener {
            startCameraIntent()
            dialog.dismiss()
        }
        val btnGallery: LinearLayout = view.findViewById(R.id.gallery_choice)
        btnGallery.setOnClickListener {
            startPhotoPicker()

            dialog.dismiss()
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun showUserImage() {
        Toast.makeText(requireContext(), "not Yet", Toast.LENGTH_LONG).show()
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

    private fun startCameraIntent() {
        if (!isInternetAvailable(requireContext())) {
            showToast("No Internet")
            return
        }
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

    private val cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (viewModel.uri.value != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.ivProfileImage.setImageURI(viewModel.uri.value)
                        viewModel.userImageChanged = true
                        binding.btnSave.visibility=View.VISIBLE
                    }
                }
            }
        }

    private fun startPhotoPicker() {
        singlePhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    val singlePhotoPicker =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.uri.value = uri
                    binding.ivProfileImage.setImageURI(uri)
                    viewModel.userImageChanged = true
                    binding.btnSave.visibility=View.VISIBLE

                }
            }
        }

    override fun onDetach() {
        super.onDetach()
        viewModel.userImageChanged = false
    }
}