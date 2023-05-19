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
    var hasChat : Boolean = false,
    var user1 : String = "",
    var user2 : String = "",
    var user1Image : String = "",
    var user2Image : String = ""

//    var lastMessage:String = "" ,
//    var lastMessageData : String = ""
    ): Parcelable