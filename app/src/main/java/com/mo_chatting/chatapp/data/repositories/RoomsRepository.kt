package com.mo_chatting.chatapp.data.repositories

import com.mo_chatting.chatapp.data.models.Room

class RoomsRepository() {

    fun getFakeRoomsList():ArrayList<Room>{
        val list = ArrayList<Room>()
        for (i in 0..10){
            list.add(Room("Room $i",false,i%5,"tempId"))
        }
        return list
    }
}