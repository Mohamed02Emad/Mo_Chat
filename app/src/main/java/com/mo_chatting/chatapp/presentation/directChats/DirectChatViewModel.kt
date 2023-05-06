package com.mo_chatting.chatapp.presentation.directChats

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.mo_chatting.chatapp.data.dataStore.DataStoreImpl
import com.mo_chatting.chatapp.data.models.DirectContact
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.data.repositories.DirectChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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


   fun getCachedChats(token:String){
       _chats.postValue( repository.getChats(token))
   }

    fun deleteChat(token:String){
        repository.deleteChat(token)
    }

    fun addChat(token:String){
        repository.addChat(token)
    }

}