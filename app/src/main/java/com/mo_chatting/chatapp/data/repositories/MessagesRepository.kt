package com.mo_chatting.chatapp.data.repositories

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.mo_chatting.chatapp.appClasses.Constants
import com.mo_chatting.chatapp.appClasses.sendFireBaseNotification
import com.mo_chatting.chatapp.data.models.*
import com.mo_chatting.chatapp.data.source.messagesRoom.MessageDao
import com.mo_chatting.chatapp.data.source.messagesRoom.MessagesDataBase
import kotlinx.coroutines.tasks.await

class MessagesRepository(val firebaseStore: FirebaseFirestore, val firebaseAuth: FirebaseAuth , val application: Context) {

    private val allRoomsRef = firebaseStore.collection(Constants.roomsCollection)

    val db = MessagesDataBase.getInstance(application)

    suspend fun insertMessageToDatabase(message: Message){
        db.myDao().insert(message)
    }

    suspend fun deleteMessageFromDatabase(message: Message){
        db.myDao().delete(message)
    }
    fun getDao():MessageDao = db.myDao()
    suspend fun addMesssageToChat(room: Room, message: Message) {
        try {
            val msgRef = firebaseStore.collection("Chats/${Constants.roomsChatCollection}/${room.roomId}")
            msgRef.add(message).await()
            sentNotificationToRoomMembers(message,room,firebaseAuth.currentUser!!.displayName!!)
            var lastMessage = if (message.messageType == MessageType.TEXT){
                message.messageText
            }else {
                "Image"
            }
            room.lastMessage = lastMessage
            updateRoom(room , true)
        } catch (_: Exception) {
            deleteMessageFromDatabase(message)
        }
    }

    private fun sentNotificationToRoomMembers(message : Message , room : Room , userName: String ){
        val destination = "/topics/${room.roomId}"
        val body = if (message.messageType == MessageType.TEXT){message.messageText}else "Sent a photo"
        sendFireBaseNotification(PushNotification(NotificationData(room.roomName,body,userName,room.roomId,firebaseAuth.currentUser!!.uid),destination))
    }


    suspend fun getServerAllMessagesForThisRoom(room: Room): ArrayList<Message> {
        val arrayList = ArrayList<Message>()
        try {
            val msgRef = firebaseStore.collection("Chats/${Constants.roomsChatCollection}/${room.roomId}")
            val result = msgRef.get().await()
            for (i in result.documents)
                arrayList.add(i.toObject<Message>()!!)

        } catch (_: Exception) {
        }
        return arrayList
    }

    fun getServerNewMessagesForThisRoom(value: QuerySnapshot?): ArrayList<Message> {
        val arrayList = ArrayList<Message>()
        for (i in value!!.documents) {
            arrayList.add(i.toObject<Message>()!!)
        }
        return arrayList
    }

     fun messageDoesNotExist(message: Message): Boolean {
       val message = db.myDao().getExactMessage(message.messageRoom,message.messageOwnerId,message.timeWithMillis)
        return message.isEmpty()
    }

    fun getLastMessageId(room: Room): Long {
        return  db.myDao().getMessagesByRoomID(room.roomId).last().messageid ?: 0
    }

    private suspend fun updateRoom(room: Room, fromChat: Boolean) {
        val map = mapMyRoom(room, fromChat)
        try {
            val roomQuery = allRoomsRef
                .whereEqualTo("roomId", room.roomId)
                .get()
                .await()
            if (roomQuery.documents.isNotEmpty()) {
                for (document in roomQuery) {
                    allRoomsRef.document(document.id).set(map, SetOptions.merge())
                }
            }

        } catch (_: Exception) {
        }

    }

    private fun mapMyRoom(room: Room, fromChat: Boolean): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["roomName"] = room.roomName
        map["roomPinState"] = room.roomPinState
        map["roomTypeImage"] = room.roomTypeImage
        map["roomId"] = room.roomId
        map["roomOwnerId"] = room.roomOwnerId
        map["hasPassword"] = room.hasPassword
        map["password"] = room.password
        map["roomBackgroundColor"] = room.roomBackgroundColor
        map["lastMessageData"] = room.lastMessageData
        map["lastMessage"] = room.lastMessage
        if (!fromChat) {
            map["listOFUsers"] = room.listOFUsers
            map["listOFUsersNames"] = room.listOFUsersNames
        }
        return map
    }
}