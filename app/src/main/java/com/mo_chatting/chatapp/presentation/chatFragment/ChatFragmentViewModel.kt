package com.mo_chatting.chatapp.presentation.chatFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide.init
import com.google.firebase.auth.FirebaseAuth
import com.mo_chatting.chatapp.data.models.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatFragmentViewModel @Inject constructor(val firebaseAuth: FirebaseAuth) : ViewModel(){

    private  var userId:String
    var isKeyboard = false


    fun changeUserID() {
        userId = if (userId == "btats") {
            firebaseAuth.currentUser!!.uid
        } else {
            "btats"
        }
    }


    private val _messageList = MutableLiveData<ArrayList<Message>>(ArrayList())
    val messageList: LiveData<ArrayList<Message>> = _messageList

    init {
        userId =firebaseAuth.currentUser!!.uid
    }

    fun addFakeMessage(message: Message){
        //_messageList.value!!.add(Message(userId,"\n\n\n\n\n\n\n\n\n\n\n\n\nfff\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nffff"," ",false))
        _messageList.value!!.add(message)
    }

    fun getUserId(): String {
        return userId
    }
}