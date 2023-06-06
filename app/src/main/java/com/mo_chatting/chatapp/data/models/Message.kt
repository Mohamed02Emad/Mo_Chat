package com.mo_chatting.chatapp.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
@Keep
data class Message(
    var messageid: Long = 0,
    var pageNumber: Long = 1,
    val messageRoom: String = "",
    val messageOwnerId: String = "",
    val messageText: String = "",
    var messageDateAndTime: String = "",
    var messageOwner: String = "",
    var likedEmoji: String? = null,
    var messageImage: String? = null,
    var messageOwnerImage: String? = null,
    var isSelected: Boolean = false,
    var isStarMessage: Boolean = false,
    var isLiked: Boolean = false,
    var isDeleted: Boolean = false,
    var messageType: MessageType = MessageType.TEXT,
    //  var usersSeenInfoList : ArrayList<MessageSeenInfo> = ArrayList(),
    @PrimaryKey val timeWithMillis: String = ""
) : Parcelable

enum class MessageState() { DELIVERED, SEEN, NOT_DELIVERED }
enum class MessageType() { VOICE_RECORD, TEXT, IMAGE, VIDEO }
