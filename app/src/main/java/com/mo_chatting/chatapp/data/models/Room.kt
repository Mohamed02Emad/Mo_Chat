package com.mo_chatting.chatapp.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
@Keep
data class Room (
    var roomName:String="",
    var roomPinState:Boolean=false,
    var roomTypeImage:Int = 0,
    var roomBackgroundColor:Int = 2,
    @PrimaryKey var roomId:String = "123",
    var roomOwnerId:String = "mohamed",
    var hasPassword :Boolean= false,
    var password:String="",
    var lastMessage:String = "" ,
    var lastMessageData : String = "",
    var listOFUsers :ArrayList<String> = ArrayList(),
    var listOFUsersNames :ArrayList<String> = ArrayList(),
    var isDirectChat :Boolean = false,
    var imgUrl :String? = null
        ): Parcelable