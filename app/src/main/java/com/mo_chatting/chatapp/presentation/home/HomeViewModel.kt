package com.mo_chatting.chatapp.presentation.home

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.data.repositories.RoomsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val appContext: Application,
    val firebaseAuth: FirebaseAuth,
    val repository: RoomsRepository
) : ViewModel() {

    private val _roomsList = MutableLiveData<ArrayList<Room>>(ArrayList())
    val roomsList: LiveData<ArrayList<Room>> = _roomsList

    var uri = MutableLiveData<Uri?>(null)

    fun resetList(value: QuerySnapshot?) {
        val arrayList = repository.getUserRooms(value)
        _roomsList.postValue(arrayList)
    }

    fun updateUserData() {
        val imageStream = appContext.contentResolver.openInputStream(uri.value!!)
        val selectedImage = BitmapFactory.decodeStream(imageStream)
        val baos = ByteArrayOutputStream()
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 90, baos)
        val data = baos.toByteArray()

        val storageRef = FirebaseStorage.getInstance()
            .getReference("user_images/${firebaseAuth.currentUser!!.uid}")
        storageRef.putBytes(data).addOnSuccessListener { taskSnapshot ->
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                val userRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(firebaseAuth.currentUser!!.uid)
                userRef.child("image").setValue(downloadUri.toString())
            }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(appContext, gso)

        googleSignInClient.signOut()
    }

    suspend fun getUserImage(): Uri? {
        var uriToReturn: Uri? = null
        try {
            val storageRef = FirebaseStorage.getInstance()
                .getReference("user_images/${firebaseAuth.currentUser!!.uid}")
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

    suspend fun createNewRoom(room: Room) {
        val roomId = getNewRoomId()
        room.roomId=roomId
        repository.createNewRoom(room)
    }

    private suspend fun getNewRoomId(): String {
        val arrayList = repository.getAllRooms()
        return (arrayList.size+1).toString()
    }
}