package com.mo_chatting.chatapp.presentation.chatFragment

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.data.models.Message
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.data.repositories.MessagesRepository
import com.mo_chatting.chatapp.data.repositories.RoomsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatFragmentViewModel @Inject constructor(
    val firebaseAuth: FirebaseAuth,
    val repository: MessagesRepository,
    val roomsRepository: RoomsRepository
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

    suspend fun resetList(value: QuerySnapshot,room: Room) {
        try {
                val list = repository.getRoomMessages(value)
            list.addAll(_messageList.value!!)
            list.sortBy { it.timeWithMillis }
            _messageList.value!!.clear()
            _messageList.value!!.addAll(list.toSet())
        } catch (e: Exception) {
        }
    }

    suspend fun getInitialData(room: Room) {
        try {
            val list = repository.getChatForRoom(room)
            list.addAll(_messageList.value!!)
            list.sortBy { it.timeWithMillis }
            _messageList.value!!.clear()
            _messageList.value!!.addAll(list.toSet())
        } catch (e: Exception) {
        }
    }

    fun getUserName(): String {
        return firebaseAuth.currentUser!!.displayName.toString()
    }

    fun getDate(): String {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        var hour = calendar.get(Calendar.HOUR_OF_DAY).toString()
        var minute : String = calendar.get(Calendar.MINUTE).toString()
        if (minute.length==1){
            minute = "0"+minute
        }
        if (hour.length==1){
            hour = "0"+hour
        }
        return "$day/$month/$year , $hour:$minute"
    }

    suspend fun updateRoomBackground(thisRoom: Room) {
       roomsRepository.updateRoom(thisRoom,true)
    }


}