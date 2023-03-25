package com.mo_chatting.chatapp.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.mo_chatting.chatapp.appClasses.Constants.roomsCollection
import com.mo_chatting.chatapp.data.models.Room
import kotlinx.coroutines.tasks.await

class RoomsRepository(val firebaseStore : FirebaseFirestore,val firebaseAuth: FirebaseAuth) {

    val roomRef = firebaseStore.collection(roomsCollection)

    suspend fun createNewRoom(room:Room){
        try {
            roomRef.add(room).await()
            // TODO: add this room to user data too
        } catch (_: Exception) {
           // Log.d(TAG, "createNewRoom: " + e.message.toString())
        }
    }


    fun updateRoom(room: Room){

    }

    fun deleteRoom(room: Room){
    }

    fun getUserRooms(value: QuerySnapshot?):ArrayList<Room>{
//        val user = firebaseAuth.currentUser!!
//        val userId = user.uid

        val arrayList = ArrayList<Room>()
        for (i in value!!.documents){
            arrayList.add(i.toObject<Room>()!!)
        }
        return arrayList
    }


}