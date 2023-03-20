package com.mo_chatting.chatapp.presentation.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.mo_chatting.chatapp.AuthActivity
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUserViews()
        setOnClicks()
    }

    private fun setUserViews() {
        binding.tvUserName.text = firebaseAuth.currentUser!!.displayName
        binding.profile.setImageURI(firebaseAuth.currentUser!!.photoUrl)
    }

    private fun setOnClicks() {
        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

            googleSignInClient.signOut()
            startActivity(Intent(requireActivity(), AuthActivity::class.java))
            requireActivity().finish()
        }

        binding.fabAdd.setOnClickListener {

        }
    }

    private fun updateUserData(){
        firebaseAuth.currentUser?.let { user ->
            //todo change name
            val name = "not me"
            //todo get image from memory
            val imageUri = Uri.parse("android.resource://${requireActivity().packageName}/${R.drawable.ic_google}")
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(imageUri)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(profileUpdates).await()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "renamed", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            e.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        }
    }

}