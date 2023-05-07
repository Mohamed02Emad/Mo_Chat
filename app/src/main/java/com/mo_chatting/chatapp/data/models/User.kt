package com.mo_chatting.chatapp.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Keep
data class User(
    var userName : String="",
    var userId : String="",
    var token:String="",
    var userAbout : String = " hi there ^_- "
)