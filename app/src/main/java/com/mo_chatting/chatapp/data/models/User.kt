package com.mo_chatting.chatapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class User(
    var userName : String,
    var userId : String,
    var token:String,
    var userAbout : String = " hi there ^_- ",
    //var userImageUri : String,
//    var lastSeen : String = "--/-/----  --:--",
//    var showOnlineStateToOthers: Boolean = true,
//    var showLastSeenToOthers: Boolean = true
) : Parcelable