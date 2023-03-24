package com.mo_chatting.chatapp.data.models

import android.net.Uri

data class Message (
    val messageOwnerId:String,
    val messageText:String,
    val messageDateAndTime:String,
    var messageOwner:String,
    var likedEmoji : String? = null,
    val messageImage:Uri?=null,
    var messageOwnerImage: Uri?=null,
    var isSelected:Boolean=false,
    var isStarMessage:Boolean=false,
    var isLiked:Boolean=false,
    var isDeleted: Boolean = false,
    val messageType: MessageType = MessageType.TEXT,
    var usersSeenInfoList : ArrayList<MessageSeenInfo> = ArrayList(),
    )

enum class MessageState(){DELIVERED,SEEN,NOT_DELIVERED}
enum class MessageType(){VOICE_RECORD,TEXT,IMAGE,VIDEO}

data class MessageSeenInfo(
    var user:User,
    var messageState:MessageState = MessageState.NOT_DELIVERED,
    var deliveryDate :String = "--/--/----",
    var deliveryTime :String = "--:--",
    var seenDate :String = "--/--/----",
    var seenTime :String = "--:--"
    )