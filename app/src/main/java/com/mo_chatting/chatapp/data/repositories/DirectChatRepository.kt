package com.mo_chatting.chatapp.data.repositories

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.mo_chatting.chatapp.appClasses.Constants
import com.mo_chatting.chatapp.data.fireBaseDataSource.FireBaseRoomsDataSource
import com.mo_chatting.chatapp.data.models.DirectContact
import com.mo_chatting.chatapp.data.models.Message
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.data.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class DirectChatRepository(
    private val firebaseStore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val application: Context,
    private val fireBaseRoomsDataSource: FireBaseRoomsDataSource
) {
    private val usersRef = firebaseStore.collection(Constants.users)
    private val allChatsRef = firebaseStore.collection(Constants.roomsCollection)


    suspend fun getChats(token: String, id: String): ArrayList<DirectContact> {
        val firends = getFriendList(id)

        return ArrayList()
    }
    fun deleteChat(token: String) {

    }
     suspend fun getFriendList(userId: String): ArrayList<String> {
        val userQuery = usersRef.whereEqualTo("userId", userId).get().await()
        for (user in userQuery) {
            return user.toObject<User>().friends
        }
        return ArrayList<String>()
    }

    fun getUserChatsFlow(): Flow<QuerySnapshot> {
        return fireBaseRoomsDataSource.setUpChatsListener()
    }

}