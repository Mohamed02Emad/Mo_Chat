package com.mo_chatting.chatapp.data.repositories

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.mo_chatting.chatapp.appClasses.Constants
import com.mo_chatting.chatapp.data.models.Message
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.data.source.messagesRoom.MessagesDataBase
import kotlinx.coroutines.tasks.await

class MessagesRepository(val firebaseStore: FirebaseFirestore, val firebaseAuth: FirebaseAuth , val application: Context) {


    val db = MessagesDataBase.getInstance(application)

    suspend fun addMesssgeToChat(room: Room, message: Message) {
        try {
            val msgRef = firebaseStore.collection("${Constants.roomsChatCollection}${room.roomId}")
            msgRef.add(message).await()
        } catch (e: Exception) {
        }
    }

    suspend fun getChatForRoom(room: Room): ArrayList<Message> {
        val arrayList = ArrayList<Message>()
        try {
            val msgRef = firebaseStore.collection("${Constants.roomsChatCollection}${room.roomId}")
            val result = msgRef.get().await()
            for (i in result.documents)
                arrayList.add(i.toObject<Message>()!!)

        } catch (e: Exception) {
        }
        return arrayList
    }

    fun getRoomNewMessages(value: QuerySnapshot?): ArrayList<Message> {
        val arrayList = ArrayList<Message>()
        for (i in value!!.documents) {
            arrayList.add(i.toObject<Message>()!!)
        }
        return arrayList
    }

}