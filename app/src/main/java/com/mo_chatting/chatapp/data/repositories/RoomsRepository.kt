package com.mo_chatting.chatapp.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.mo_chatting.chatapp.appClasses.Constants
import com.mo_chatting.chatapp.appClasses.Constants.roomsCollection
import com.mo_chatting.chatapp.data.models.Message
import com.mo_chatting.chatapp.data.models.Room
import kotlinx.coroutines.tasks.await

class RoomsRepository(val firebaseStore: FirebaseFirestore, val firebaseAuth: FirebaseAuth) {

    val allRoomsRef = firebaseStore.collection("$roomsCollection")

    suspend fun createNewRoom(room: Room) {
        try {
            room.listOFUsers.add(firebaseAuth.currentUser!!.uid)
            allRoomsRef.add(room).await()
            createChatForRoom(room)
        } catch (_: Exception) {
            // Log.d(TAG, "createNewRoom: " + e.message.toString())
        }
    }

    suspend fun createChatForRoom(room: Room) {
        try {
            val msgRef = firebaseStore.collection("${Constants.roomsChatCollection}${room.roomId}")
            msgRef.add(
                Message(
                    messageOwner = "Mo_Chat",
                    messageOwnerId = "firebase",
                    messageText = firebaseAuth.currentUser!!.displayName.toString()+" Created this Room",
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
            updateRoom(it)
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

    suspend fun updateRoom(room: Room) {
        val map = mapMyRoom(room)
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
        val list = room.listOFUsers
        if (list.size==1){
            val roomQuery = allRoomsRef
                .whereEqualTo("roomId", room.roomId)
                .get()
                .await()

            for (i in roomQuery.documents){
                val docRef = allRoomsRef.document(i.id)
                docRef.delete().await()
            }
        }else {
            list.remove(firebaseAuth.currentUser!!.uid)
            room.listOFUsers = list
            updateRoom(room)
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

    private fun mapMyRoom(room: Room): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["roomName"] = room.roomName
        map["roomPinState"] = room.roomPinState
        map["roomTypeImage"] = room.roomTypeImage
        map["roomId"] = room.roomId
        map["roomOwnerId"] = room.roomOwnerId
        map["hasPassword"] = room.hasPassword
        map["password"] = room.password
        map["listOFUsers"] = room.listOFUsers
        return map
    }

}