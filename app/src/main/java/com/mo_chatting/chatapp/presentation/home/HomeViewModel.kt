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
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.mo_chatting.chatapp.data.dataStore.DataStoreImpl
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.data.repositories.RoomsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.net.URI
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class HomeViewModel @Inject constructor(
    val appContext: Application,
    val firebaseAuth: FirebaseAuth,
    val repository: RoomsRepository
) : ViewModel() {

    @Inject
    lateinit var dataStore: DataStoreImpl

    private val _roomsList = MutableLiveData<ArrayList<Room>>(ArrayList())
    val roomsList: LiveData<ArrayList<Room>> = _roomsList

    var uri = MutableLiveData<Uri?>(null)

    fun resetList(value: QuerySnapshot?) {
        try {
            val arrayList = repository.getUserRooms(value)
            _roomsList.postValue(arrayList)
        } catch (_: Exception) {
        }
    }

    fun updateUserData(){
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
                CoroutineScope(Dispatchers.IO).launch {
                    setUserImageAtDataStoreUri(downloadUri)
                }
            }
        }
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

    suspend fun getUserImage(): String {
        var uriToReturn: String = "null"
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

    suspend fun createNewRoom(room: Room) {
        var roomId = getNewRoomId()
        while (!isRoomValidId(roomId)) {
            roomId = getNewRoomId()
        }
        room.roomId = roomId
        repository.createNewRoom(room)
    }

    private suspend fun isRoomValidId(roomId: String): Boolean {
        val roomsList = repository.getAllRooms()
        return !roomsList.any { it.roomId == roomId }
    }

    private suspend fun getNewRoomId(): String {
        val numchars = 8
        val r = Random()
        val sb = StringBuffer()
        while (sb.length < numchars) {
            sb.append(Integer.toHexString(r.nextInt()))
        }
        return sb.toString().substring(0, numchars)
    }

    suspend fun checkIfRoomExist(roomId: String): Room? {
        return repository.checkIfRoomExist(roomId)
    }

    suspend fun joinRoom(room: Room) {
        repository.joinRoom(room)
    }

    suspend fun getUserName(): String {
        return dataStore.getUserName()
    }

    suspend fun setUserName() {
        val userName = firebaseAuth.currentUser?.let { it.displayName.toString() } ?: "null"
        dataStore.setUserName(userName)
    }

    suspend fun getUserImageFromDataStore(): Uri? {
        val data = dataStore.getUserImage()
        if (data == "null" || data.isBlank()){
            return null
        }else{
            return Uri.parse(data)
        }
    }

    suspend fun setUserImageAtDataStore() {
        dataStore.setUserImage(getUserImage())
    }
    suspend fun setUserImageAtDataStoreUri(uri: Uri) {
        dataStore.setUserImage(uri.toString())
    }

    suspend fun deleteRoom(room:Room){
        //this method just leave the room right now
        repository.deleteRoom(room)
    }

    suspend fun updateRoom(room: Room) {
       repository.updateRoom(room,false)
    }

    fun updateUSerName(newName: String) {
        firebaseAuth.currentUser?.let { user ->
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(profileUpdates).await()
                    updateRoomByName(newName,firebaseAuth.currentUser!!.uid)
                    dataStore.setUserName(newName)
                } catch (_: Exception) {

                }
            }
        }
    }

    private suspend fun updateRoomByName(newName: String,uid : String) {
        val list = ArrayList<Room>().apply {addAll(_roomsList.value!!) }
        for (room in list){
            repository.updateRoomForUserName(room,newName,uid)
        }
    }
}