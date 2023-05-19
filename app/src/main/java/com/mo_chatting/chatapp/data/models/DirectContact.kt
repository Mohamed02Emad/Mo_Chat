package com.mo_chatting.chatapp.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.errorprone.annotations.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
@Keep
data class DirectContact(
    @PrimaryKey val chatToken: String = "",
    val users : ArrayList<String> = ArrayList(),
//    var lastMessage:String = "" ,
//    var lastMessageData : String = ""
    ): Parcelable