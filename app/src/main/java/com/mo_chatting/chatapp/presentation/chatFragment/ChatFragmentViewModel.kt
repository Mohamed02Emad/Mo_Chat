package com.mo_chatting.chatapp.presentation.chatFragment

import android.app.Application
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
    val appContext: Application,
    val repository: MessagesRepository
) : ViewModel() {

    private var userId: String
    var isKeyboard = false

    private val _messageList = MutableLiveData<ArrayList<Message>>(ArrayList())
    val messageList: LiveData<ArrayList<Message>> = _messageList

    init {
        userId = firebaseAuth.currentUser!!.uid
    }

    suspend fun sendMessage(message: Message,room: Room) {
        repository.addMesssgeToChat(message = message, room = room)
        //_messageList.value!!.add(message)
    }

    suspend fun getRoomMesseges(room: Room):ArrayList<Message>{
        return repository.getChatForRoom(room)
    }

    fun getUserId(): String {
        return userId
    }

    suspend fun resetList(room: Room) {
        try {
            val list = repository.getChatForRoom(room = room)
            list.sortBy { it.messageDateAndTime }
            val list2 =ArrayList<Message>()
            list2.addAll(list)
            list2.addAll(_messageList.value!!)
            _messageList.value!!.addAll(list2.toSet())
        }catch (e:Exception){}
    }
}