package com.mo_chatting.chatapp.presentation.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.mo_chatting.chatapp.data.dataStore.DataStoreImpl
import com.mo_chatting.chatapp.data.repositories.RoomsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val appContext: Application,
    val firebaseAuth: FirebaseAuth,
    private val repository: RoomsRepository
) : ViewModel() {

    @Inject
    lateinit var dataStore: DataStoreImpl


    suspend fun getUserName(): String {
        return dataStore.getUserName()
    }

    suspend fun setUserName() {
        val userName = firebaseAuth.currentUser?.let { it.displayName.toString() } ?: "null"
        dataStore.setUserName(userName)
    }

    suspend fun getUserImageFromDataStore(): Uri? {
        val data = dataStore.getUserImage()
        return if (data == "null" || data.isBlank()) {
            null
        } else {
            Uri.parse(data)
        }
    }

    suspend fun setUserImageAtDataStore() {
        dataStore.setUserImage(getUserImage())
    }

    private suspend fun getUserImage(): String {
        var uriToReturn = "null"
        try {
            val storageRef = FirebaseStorage.getInstance()
                .getReference("user_images/${firebaseAuth.currentUser!!.uid}")
            storageRef.downloadUrl.apply {
                addOnSuccessListener { downloadUri ->
                    uriToReturn = downloadUri.toString()
                }
                await()
            }
        } catch (_: Exception) {

        }
        return uriToReturn
    }

    suspend fun signOut() {
        firebaseAuth.signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(appContext, gso)
        googleSignInClient.signOut()
        dataStore.clearAll()
    }


}