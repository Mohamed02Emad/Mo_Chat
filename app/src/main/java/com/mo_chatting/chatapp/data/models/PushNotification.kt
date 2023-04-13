package com.mo_chatting.chatapp.data.models

import com.google.gson.annotations.SerializedName

data class PushNotification (
    @SerializedName("data")
    val notification : NotificationData,
    @SerializedName("to")
    val to:String
)