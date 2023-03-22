package com.mo_chatting.chatapp.data.models

data class Message (
    val messageOwner:String,
    val messageContent:String,
    val messageDate:String,
    var isDeleted: Boolean = false
    )