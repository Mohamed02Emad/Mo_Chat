package com.mo_chatting.chatapp.data.repositories

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.storage.FirebaseStorage
import com.mo_chatting.chatapp.appClasses.Constants
import com.mo_chatting.chatapp.appClasses.Constants.roomsCollection
import com.mo_chatting.chatapp.data.models.Message
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.data.source.messagesRoom.MessagesDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RoomsRepository(
    val firebaseStore: FirebaseFirestore,
    val firebaseAuth: FirebaseAuth,
    val application: Context
) {

    val allRoomsRef = firebaseStore.collection("$roomsCollection")
    val db = MessagesDataBase.getInstance(application)


    suspend fun createNewRoom(room: Room) {
        try {
            room.listOFUsers.add(firebaseAuth.currentUser!!.uid)
            room.listOFUsersNames.add(firebaseAuth.currentUser!!.displayName.toString())
            allRoomsRef.add(room).await()
            createChatForRoom(room)
            joinRoomNotifications(roomId = room.roomId)
        } catch (_: Exception) {
            // Log.d(TAG, "createNewRoom: " + e.message.toString())
        }
    }

    private suspend fun createChatForRoom(room: Room) {
        try {
            val msgRef = firebaseStore.collection("${Constants.roomsChatCollection}${room.roomId}")
            msgRef.add(
                Message(
                    messageOwner = "Mo Chat",
                    messageOwnerId = "firebase",
                    messageRoom = room.roomId,
                    messageText = firebaseAuth.currentUser!!.displayName.toString() + " Created this Room",
                    messageDateAndTime = "--/--/----  --:--"
                )
            ).await()
        } catch (e: Exception) {
        }
    }

    suspend fun joinRoom(room: Room) {
        room.let {
            if (it.listOFUsers.any { it == firebaseAuth.currentUser!!.uid }) return
            it.listOFUsers.add(firebaseAuth.currentUser!!.uid)
            it.listOFUsersNames.add(firebaseAuth.currentUser!!.displayName.toString())
            updateRoom(it, false)
        }
        joinRoomNotifications(roomId = room.roomId)
    }

    private fun joinRoomNotifications(roomId: String){
        Firebase.messaging.subscribeToTopic(roomId)
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d("mohamed", msg)
                // Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
    }

    suspend fun checkIfRoomExist(roomId: String): Room? {
        val list = getAllRooms()
        for (i in list) {
            if (i.roomId == roomId) {
                return i
            }
        }
        return null
    }

    suspend fun updateRoom(room: Room, fromChat: Boolean) {
        val map = mapMyRoom(room, fromChat)
        try {
            val roomQuery = allRoomsRef
                .whereEqualTo("roomId", room.roomId)
                .get()
                .await()
            if (roomQuery.documents.isNotEmpty()) {
                for (document in roomQuery) {
                    allRoomsRef.document(document.id).set(map, SetOptions.merge())
                }
            }
        } catch (_: Exception) {
        }

    }

    suspend fun deleteRoom(room: Room) {
        val msgRef = firebaseStore.collection("${Constants.roomsChatCollection}${room.roomId}")
        deleteCachedMessages(room.roomId)
        val list = room.listOFUsers
        val nameList = room.listOFUsersNames

        if (list.size == 1) {
            try {
                deleteRoomImages(room)
                val roomQuery = allRoomsRef
                    .whereEqualTo("roomId", room.roomId)
                    .get()
                    .await()

                for (i in roomQuery.documents) {
                    val docRef = allRoomsRef.document(i.id)
                    docRef.delete().await()
                }
                msgRef.get().addOnSuccessListener { documents ->
                    val batch = firebaseStore.batch()
                    for (document in documents) {
                        batch.delete(document.reference)
                    }
                    batch.commit()
                }

            } catch (_: Exception) {
            }

        } else {
            list.remove(firebaseAuth.currentUser!!.uid)
            nameList.remove(firebaseAuth.currentUser!!.displayName.toString())
            room.listOFUsers = list
            room.listOFUsersNames = nameList
            updateRoom(room, false)
        }
    }

    private fun deleteRoomImages(room:Room){
        val storageRef = FirebaseStorage.getInstance().getReference("chat_images_${room.roomId}")
        CoroutineScope(Dispatchers.IO).launch {
            storageRef.listAll().addOnSuccessListener { listResult ->
                listResult.items.forEach { item ->
                    item.delete()
                }
            }
        }
    }

    fun getUserRooms(value: QuerySnapshot?): ArrayList<Room> {
        val userId = firebaseAuth.currentUser!!.uid
        val arrayList = ArrayList<Room>()
        for (i in value!!.documents) {
            if (i.toObject<Room>()!!.listOFUsers.contains(userId)) {
                arrayList.add(i.toObject<Room>()!!)
            }
        }
        return arrayList
    }

    suspend fun getAllRooms(): ArrayList<Room> {
        val arrayList = ArrayList<Room>()
        try {
            val result = allRoomsRef.get().await()
            val list = result.documents
            for (i in list) {
                arrayList.add(i.toObject<Room>()!!)
            }
        } catch (e: Exception) {
        }
        return arrayList
    }

    private fun mapMyRoom(room: Room, fromChat: Boolean): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["roomName"] = room.roomName
        map["roomPinState"] = room.roomPinState
        map["roomTypeImage"] = room.roomTypeImage
        map["roomId"] = room.roomId
        map["roomOwnerId"] = room.roomOwnerId
        map["hasPassword"] = room.hasPassword
        map["password"] = room.password
        map["roomBackgroundColor"] = room.roomBackgroundColor
        if (!fromChat) {
            map["listOFUsers"] = room.listOFUsers
            map["listOFUsersNames"] = room.listOFUsersNames
        }
        return map
    }

    suspend fun updateRoomForUserName(room: Room, newName: String, uid: String) {
        for (i in 0 until room.listOFUsers.size) {
            if (room.listOFUsers[i] == uid) {
                room.listOFUsersNames[i] = newName
                break
            }
        }
        updateRoom(room, false)
    }

    suspend fun deleteCachedMessages(roomId: String) = db.myDao().deleteAll(roomId)

}