package com.mo_chatting.chatapp.presentation.groupChat

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.mo_chatting.chatapp.data.dataStore.DataStoreImpl
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.data.repositories.RoomsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val appContext: Application,
    val firebaseAuth: FirebaseAuth,
    private val repository: RoomsRepository
) : ViewModel() {

    @Inject
    lateinit var dataStore: DataStoreImpl

    private val _roomsList = MutableLiveData<ArrayList<Room>>(ArrayList())
    val roomsList: LiveData<ArrayList<Room>> = _roomsList

    val roomsFlow: Flow<QuerySnapshot> = repository.getUserRoomsFlow()

    var uri = MutableLiveData<Uri?>(null)

    fun addNewRoomsFromFireBaseToRoomList(newRooms: QuerySnapshot?) {
        try {
            val userId = firebaseAuth.currentUser!!.uid
            val arrayList = ArrayList<Room>()
            for (i in newRooms!!.documents) {
                if (i.toObject<Room>()!!.listOFUsers.contains(userId)) {
                    arrayList.add(i.toObject<Room>()!!)
                }
            }
            _roomsList.postValue(arrayList)
        } catch (_: Exception) {
        }
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

    private  fun getNewRoomId(): String {
        val numChars = 8
        val r = Random()
        val sb = StringBuffer()
        while (sb.length < numChars) {
            sb.append(Integer.toHexString(r.nextInt()))
        }
        return sb.toString().substring(0, numChars)
    }

    suspend fun checkIfRoomExist(roomId: String): Room? {
        return repository.checkIfRoomExist(roomId)
    }

    suspend fun joinRoom(room: Room) {
        repository.joinRoom(room)
    }

    suspend fun deleteRoom(room: Room) {
        repository.deleteRoom(room)
    }

    suspend fun updateRoom(room: Room) {
        repository.updateRoom(room, false)
    }

}