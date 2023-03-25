package com.mo_chatting.chatapp.data.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mo_chatting.chatapp.appClasses.Constants.roomsCollection
import com.mo_chatting.chatapp.data.models.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RoomsRepository(val firebaseStore : FirebaseFirestore,val firebaseAuth: FirebaseAuth) {

    val roomRef = firebaseStore.collection(roomsCollection)

    suspend fun createNewRoom(room:Room){
        try {
            Log.d("mohamed", "createNewRoom: " + "before2")
            roomRef.add(room).await()
            Log.d("mohamed", "createNewRoom: " + "no error")
        } catch (e: Exception) {
            Log.d("mohamed", "createNewRoom: " + e.message.toString())
        }
    }



    fun updateRoom(room: Room){

    }

    fun deleteRoom(room: Room){

    }

    fun getUserRooms(){
        val user = firebaseAuth.currentUser!!
        val userId = user.uid
    }

    fun getFakeRoomsList():ArrayList<Room>{
        val list = ArrayList<Room>()
        for (i in 0..10){
            list.add(Room("Room $i",false,i%5,"tempId","bla"))
        }
        return list
    }
}