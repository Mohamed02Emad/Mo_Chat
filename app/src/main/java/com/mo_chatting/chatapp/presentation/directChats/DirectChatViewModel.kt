package com.mo_chatting.chatapp.presentation.directChats

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.mo_chatting.chatapp.data.dataStore.DataStoreImpl
import com.mo_chatting.chatapp.data.models.DirectContact
import com.mo_chatting.chatapp.data.repositories.DirectChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DirectChatViewModel @Inject constructor(
    val appContext: Application,
    val firebaseAuth: FirebaseAuth,
    private val repository: DirectChatRepository
) : ViewModel() {

    @Inject
    lateinit var dataStore: DataStoreImpl

    private val _chats = MutableLiveData<ArrayList<DirectContact>>(ArrayList())
    val chats: LiveData<ArrayList<DirectContact>> = _chats

    val chatsFlow: Flow<QuerySnapshot> = repository.getUserChatsFlow()


    suspend fun getCachedChats(token: String) {
        val id = dataStore.getUserId()!!
        _chats.postValue(repository.getChats(token, id))
    }

    fun deleteChat(token: String) {
        repository.deleteChat(token)
    }

    fun addChat(token: String) {
        repository.addChat(token)
    }

    suspend fun addNewChatsFromFireBaseToChatList(newChats: QuerySnapshot) {
        try {
            val userId = dataStore.getUserId()!!
            val arrayList = java.util.ArrayList<DirectContact>()
            val userFriends = repository.getFriendList(userId)
            for (i in newChats!!.documents) {
                val chat = i.toObject<DirectContact>()!!
                val users = ArrayList<String>()
                users.addAll(chat.users)
                users.remove(userId)
                val otherUser = users.first()
                if (chat.users.contains(userId) && userFriends.contains(otherUser)
                ) {
                    arrayList.add(chat)
                }
            }
            _chats.postValue(arrayList)
        } catch (_: Exception) {

        }
    }

    suspend fun getUserName(): String {
        return dataStore.getUserName()
    }


}