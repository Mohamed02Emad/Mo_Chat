package com.mo_chatting.chatapp.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mo_chatting.chatapp.data.models.Room

class RoomsRepository(val firebaseStore : FirebaseFirestore,val firebaseAuth: FirebaseAuth) {


    fun createNewRoom(room:Room){

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