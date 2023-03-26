package com.mo_chatting.chatapp.presentation.chatFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.mo_chatting.chatapp.data.models.Message
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.data.repositories.MessagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatFragmentViewModel @Inject constructor(
    val firebaseAuth: FirebaseAuth,
    val repository: MessagesRepository
) : ViewModel() {

    private var userId: String
    var isKeyboard = false

    private val _messageList = MutableLiveData<ArrayList<Message>>(ArrayList())
    val messageList: LiveData<ArrayList<Message>> = _messageList

    init {
        userId = firebaseAuth.currentUser!!.uid
    }

    suspend fun sendMessage(message: Message, room: Room) {
        repository.addMesssgeToChat(message = message, room = room)
    }
    fun getUserId(): String {
        return userId
    }

    suspend fun resetList(value: QuerySnapshot) {
        try {
            val list = repository.getRoomMessages(value)
            list.sortBy { it.messageDateAndTime }
            _messageList.value!!.addAll(list)
        } catch (e: Exception) {
        }
    }

    suspend fun getInitialData(room: Room) {
        try {
            val list = repository.getChatForRoom(room)
            list.sortBy { it.messageDateAndTime }
            _messageList.value!!.addAll(list.toSet())
        } catch (e: Exception) {
        }
    }
}