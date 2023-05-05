package com.mo_chatting.chatapp.presentation.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mo_chatting.chatapp.databinding.FragmentUserImageDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class UserImageDialog(
    val userId: String,
    val userName: String,
    val imageUri: String? = null,
    val isImageView: Boolean = false,
    val imgFromProfile: Uri? = null
) : DialogFragment() {
    @Inject
    lateinit var firestore: FirebaseFirestore

    private lateinit var binding: FragmentUserImageDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserImageDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
        setDimentions()
    }

    private fun setViews() {
        CoroutineScope(Dispatchers.IO).launch {
            val uri = if (isImageView) {
                getImage(imageUri)
            } else if (imgFromProfile != null) {
                imgFromProfile
            } else {
                getUserImage()
            }
            withContext(Dispatchers.Main) {
                Glide.with(requireContext())
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.ivUserImage)
            }

        }
    }

    private suspend fun getImage(messageImage: String?): Uri? {
        var uriToReturn: Uri? = null
        try {

            val storageRef = FirebaseStorage.getInstance()
                .getReference(messageImage.toString())
            storageRef.downloadUrl.apply {
                addOnSuccessListener { downloadUri ->
                    uriToReturn = downloadUri
                }
                await()
            }
        } catch (_: Exception) {
        }
        return uriToReturn
    }

    private fun setDimentions() {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        this.dialog!!.window!!.setLayout(((10 * width) / 11), (5 * height) / 10)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    suspend fun getUserImage(): Uri? {
        var uriToReturn: Uri? = null
        try {
            val storageRef = FirebaseStorage.getInstance()
                .getReference("user_images/${userId}")
            storageRef.downloadUrl.apply {
                addOnSuccessListener { downloadUri ->
                    uriToReturn = downloadUri
                }
                await()
            }
        } catch (_: Exception) {
        }
        return uriToReturn
    }
}