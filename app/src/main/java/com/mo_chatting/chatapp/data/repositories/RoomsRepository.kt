package com.mo_chatting.chatapp.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.mo_chatting.chatapp.appClasses.Constants.roomsCollection
import com.mo_chatting.chatapp.data.models.Room
import kotlinx.coroutines.tasks.await

class RoomsRepository(val firebaseStore: FirebaseFirestore, val firebaseAuth: FirebaseAuth) {

    // val roomRef = firebaseStore.collection("$roomsCollection${firebaseAuth.currentUser!!.uid}")
    val allRoomsRef = firebaseStore.collection("$roomsCollection")

    suspend fun createNewRoom(room: Room) {
        try {
            room.listOFUsers.add(firebaseAuth.currentUser!!.uid)
            allRoomsRef.add(room).await()
        } catch (_: Exception) {
            // Log.d(TAG, "createNewRoom: " + e.message.toString())
        }
    }

    suspend fun joinRoom(roomId: String){
        val list = getAllRooms()
        var room :Room? = null
        for (i in list ){
            if (i.roomId == roomId){
                room=i
                break
            }
        }
        room?.let {
            it.listOFUsers.add(firebaseAuth.currentUser!!.uid)
            updateRoom(it)
        }
    }

    fun updateRoom(room: Room) {
        // TODO: update for all users
    }

    fun deleteRoom(room: Room) {
        // TODO: if the owner deleted it remove it from all users else remove from this exact user
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


}