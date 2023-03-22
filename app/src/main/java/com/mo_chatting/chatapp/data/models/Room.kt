package com.mo_chatting.chatapp.data.models

data class Room (
    var roomName:String,
    var roomPinState:Boolean,
    var roomTypeImage:Int,
    var listOFUsers :ArrayList<User> = ArrayList()
        )