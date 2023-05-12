package com.mo_chatting.chatapp.data.repositories

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mo_chatting.chatapp.data.models.DirectContact
import com.mo_chatting.chatapp.presentation.directChats.DirectChats

class DirectChatRepository(
    private val firebaseStore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val application: Context
) {
    fun getChats(token: String): ArrayList<DirectContact> {
    return ArrayList()
    }

    fun deleteChat(token: String) {

    }

    fun addChat(token: String) {

    }

}