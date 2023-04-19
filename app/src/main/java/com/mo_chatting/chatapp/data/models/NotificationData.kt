package com.mo_chatting.chatapp.data.models

import androidx.annotation.Keep

@Keep
data class NotificationData (
    val title: String,
    val body: String,
    val userName:String,
    val roomId:String,
    val ownerId:String
        )