package com.mo_chatting.chatapp.data.models

import androidx.annotation.Keep

@Keep
data class User(
    var userName: String = "",
    var userId: String = "",
    var token: String = "",
    var userAbout: String = " hi there ^_- ",
    var imageUrl: String = "null",
    var isOnline: Boolean = false,
    var friends: ArrayList<String> = ArrayList()
)