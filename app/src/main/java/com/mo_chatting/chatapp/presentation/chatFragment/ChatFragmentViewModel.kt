package com.mo_chatting.chatapp.presentation.chatFragment

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.view.ViewCompat.setBackground
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.mo_chatting.chatapp.appClasses.Constants
import com.mo_chatting.chatapp.data.dataStore.DataStoreImpl
import com.mo_chatting.chatapp.data.models.Message
import com.mo_chatting.chatapp.data.models.MessageType
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.data.repositories.MessagesRepository
import com.mo_chatting.chatapp.data.repositories.RoomsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatFragmentViewModel @Inject constructor(
    val firebaseAuth: FirebaseAuth,
    val appContext: Application,
    val repository: MessagesRepository,
    val roomsRepository: RoomsRepository
) : ViewModel() {

    @Inject
    lateinit var dataStore: DataStoreImpl

    var uri = MutableLiveData<Uri?>(null)

    private var userId: String = firebaseAuth.currentUser!!.uid
    var isKeyboard = false

    private val _messageList = MutableLiveData<ArrayList<Message>>(ArrayList())
    val messageList: LiveData<ArrayList<Message>> = _messageList

    suspend fun sendMessage(message: Message, room: Room) {
        repository.addMesssgeToChat(message = message, room = room)
    }

    fun getUserId(): String {
        return userId
    }

    suspend fun resetList(value: QuerySnapshot, room: Room) {
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
        var minute: String = calendar.get(Calendar.MINUTE).toString()
        if (minute.length == 1) {
            minute = "0" + minute
        }
        if (hour.length == 1) {
            hour = "0" + hour
        }
        return "$day/$month/$year , $hour:$minute"
    }

    suspend fun updateRoomBackground(thisRoom: Room) {
        roomsRepository.updateRoom(thisRoom, true)
    }

    suspend fun uploadImage(room: Room) {
        try {
            val message = Message(
                getUserId(),
                messageDateAndTime = getDate(),
                messageOwner = getUserName(),
                timeWithMillis = System.currentTimeMillis().toString(),
                messageType = MessageType.IMAGE,
                messageImage = "chat_images_${room.roomId}/${System.currentTimeMillis()}"
            )

            val imageStream = appContext.contentResolver.openInputStream(uri.value!!)
            val selectedImage = BitmapFactory.decodeStream(imageStream)
            val baos = ByteArrayOutputStream()

            if (dataStore.getLowImageQuality()) {
                val newWidth = selectedImage.width / 2
                val newHeight = selectedImage.height / 2
                val resizedBitmap =
                    Bitmap.createScaledBitmap(selectedImage, newWidth, newHeight, false)
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos)
            } else {
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            }

            val data = baos.toByteArray()

            val storageRef = FirebaseStorage.getInstance()
                .getReference("chat_images_${room.roomId}/${message.timeWithMillis}")
            storageRef.putBytes(data).await()
            repository.addMesssgeToChat(room, message)
        } catch (e: Exception) {
            Log.d(Constants.TAG, "uploadImage: " + e.message.toString())
        }
    }

    suspend fun newColor(thisRoom: Room): Room {
        var x = thisRoom.roomBackgroundColor
        x++
        if (x > 7) x = 0
        thisRoom.roomBackgroundColor = x
        return thisRoom
    }
}