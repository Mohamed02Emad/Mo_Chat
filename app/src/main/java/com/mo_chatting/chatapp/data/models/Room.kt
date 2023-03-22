package com.mo_chatting.chatapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Room (
    var roomName:String,
    var roomPinState:Boolean,
    var roomTypeImage:Int,
    val roomId:String,
    //var listOFUsers :ArrayList<User> = ArrayList()
        ): Parcelable