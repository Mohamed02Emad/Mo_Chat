package com.mo_chatting.chatapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Room (
    var roomName:String="",
    var roomPinState:Boolean=false,
    var roomTypeImage:Int = 0,
    var roomBackgroundColor:Int = 0,
    //todo add it in viewModel
    var roomId:String = "123",
    var roomOwnerId:String = "mohamed",
    var hasPassword :Boolean= false,
    var password:String="",
    var listOFUsers :ArrayList<String> = ArrayList()
        ): Parcelable