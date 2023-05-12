package com.mo_chatting.chatapp.presentation.groupChat

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.mo_chatting.chatapp.appClasses.Constants
import com.mo_chatting.chatapp.data.dataStore.DataStoreImpl
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.data.models.User
import com.mo_chatting.chatapp.data.repositories.RoomsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val appContext: Application,
    val firebaseAuth: FirebaseAuth,
     firebaseFirestore: FirebaseFirestore,
    private val repository: RoomsRepository
) : ViewModel() {
    @Inject
    lateinit var dataStore: DataStoreImpl
    private val usersRef = firebaseFirestore.collection(Constants.users)

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

    private fun getNewRoomId(): String {
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

    private suspend fun createUser() {
        var id : String
        if (userExists()) return
        do {
            id = createUserId()
        } while (!checkIfUserIdExist(id))

        saveUser(id)

    }

    private suspend fun userExists(): Boolean = try {
        val userQuery = usersRef
            .whereEqualTo("token", firebaseAuth.currentUser!!.uid)
            .get()
            .await()
        for (user in userQuery) {
            val user = user.toObject<User>()
            dataStore.saveUserId(user.userId)
            dataStore.setUserImage(user.imageUrl)
        }
        userQuery.documents.isNotEmpty()
    } catch (_: Exception) {
        true
    }

    private suspend fun saveUser(id: String) {
        uploadUserId(id)
        dataStore.saveUserId(id)

    }

    private suspend fun uploadUserId(id: String) {
        try {
            val user = User(
                userName = firebaseAuth.currentUser!!.displayName!!,
                userId = id,
                token = firebaseAuth.currentUser!!.uid
            )
            usersRef.add(user).await()
        } catch (_: Exception) {
        }
    }

    private suspend fun checkIfUserIdExist(userId: String): Boolean = try {
        val userQuery = usersRef
            .whereEqualTo("userId", userId)
            .get()
            .await()
        userQuery.documents.isEmpty()
    } catch (_: Exception) {
        false
    }

    private fun createUserId(): String {
        val numChars = 8
        val r = Random()
        val sb = StringBuffer()
        while (sb.length < numChars) {
            sb.append(Integer.toHexString(r.nextInt()))
        }
        return sb.toString().substring(0, numChars)
    }

    suspend fun setupUserId() {
        createUser()

    }

}