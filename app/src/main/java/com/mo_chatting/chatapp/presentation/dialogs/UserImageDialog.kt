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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.databinding.FragmentUserImageDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class UserImageDialog(val userId: String, val userName: String) : DialogFragment() {
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
            val uri = getUserImage()
            withContext(Dispatchers.Main) {
                Glide.with(requireContext())
                    .load(uri)
                    .error(R.drawable.ic_profile)
                    .override(500, 400)
                    .into(binding.ivUserImage)
            }
        }
    }

    private fun setDimentions() {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        this.dialog!!.window!!.setLayout(((9 * width) / 10), (7 * height) / 10)
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