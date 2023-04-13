package com.mo_chatting.chatapp.data.models

data class NotificationData (
    val title: String,
    val body: String,
    val userName:String,
    val roomId:String,
    val ownerId:String
        )