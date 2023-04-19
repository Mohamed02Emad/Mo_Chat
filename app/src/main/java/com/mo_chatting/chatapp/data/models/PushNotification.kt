package com.mo_chatting.chatapp.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PushNotification (
    @SerializedName("data")
    val notification : NotificationData,
    @SerializedName("to")
    val to:String
)