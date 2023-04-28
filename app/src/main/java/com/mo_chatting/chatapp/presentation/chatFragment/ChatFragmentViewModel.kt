package com.mo_chatting.chatapp.presentation.chatFragment

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.mo_chatting.chatapp.data.dataStore.DataStoreImpl
import com.mo_chatting.chatapp.data.models.Message
import com.mo_chatting.chatapp.data.models.MessageType
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.data.pagingSource.MessagePagingSource
import com.mo_chatting.chatapp.data.repositories.MessagesRepository
import com.mo_chatting.chatapp.data.repositories.RoomsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.ZoneId
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

    lateinit var thisRoom: Room

    lateinit var pagingSource: MessagePagingSource

    lateinit var pagingAdapterList: List<Message>
    val messages =
        Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = false,
                prefetchDistance = 1
            )
        ) {
            provideMessagePagingSource()
        }.flow.cachedIn(viewModelScope)


    var uri = MutableLiveData<Uri?>(null)
    val itemInserted = MutableLiveData(false)


    private var userId: String = firebaseAuth.currentUser!!.uid
    var isKeyboard = false

    suspend fun getInitialMessages(room: Room): Set<Message>? =
        try {
            val cashedList = getCachedMessages(room)
            val newList = repository.getServerAllMessagesForThisRoom(room)
            newList.removeAll(cashedList.toSet())
            newList.toSet()
        } catch (e: Exception) {
            null
        }

    suspend fun getNewMessages(value: QuerySnapshot, room: Room) =
        try {
            val list = repository.getServerNewMessagesForThisRoom(value)
            viewModelScope.launch(Dispatchers.IO) {
                showNewMessages(list)
            }
        } catch (e: Exception) {
            null
        }

    private fun provideMessagePagingSource(): MessagePagingSource {
        pagingSource = MessagePagingSource(repository.getDao(), thisRoom.roomId)
        return pagingSource
    }

    fun getUserId(): String {
        return userId
    }

    fun getUserName(): String {
        return firebaseAuth.currentUser!!.displayName.toString()
    }

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
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

    suspend fun uploadImage(room: Room) {
        try {
            val messageTimeInMillis = System.currentTimeMillis().toString()
            var imageUrl: String? = null

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

                .getReference("chat_images_${room.roomId}/${messageTimeInMillis}")
            storageRef.putBytes(data).await()

            storageRef.downloadUrl.apply {
                addOnSuccessListener { downloadUri ->
                    imageUrl = downloadUri.toString()
                }
                await()
            }
            if (imageUrl == null) {
                uploadImage(room)
                return
            }
            val message = Message(
                messageRoom = room.roomId,
                messageOwnerId = getUserId(),
                messageDateAndTime = getCurrentDate(),
                messageOwner = getUserName(),
                timeWithMillis = messageTimeInMillis,
                messageType = MessageType.IMAGE,
                messageImage = imageUrl
            )
            message.messageid = getMessageId()
            repository.addMesssageToChat(room, message)
        } catch (e: Exception) {
            // Log.d(Constants.TAG, "uploadImage: " + e.message.toString())
        }
    }

    suspend fun sendMessage(message: Message, room: Room) {
        cacheNewMessageSent(message)
        message.messageid = getMessageId()
        repository.addMesssageToChat(message = message, room = room)
    }

    private fun getMessageId(): Long {
      val lastMessageId = repository.getLastMessageId(thisRoom)
      return (lastMessageId + 1)
    }

    fun getDateForAllCountries(): String {
        val timeZone = ZoneId.systemDefault()
        val currentDateTime = LocalDateTime.now(timeZone)
        // Calculate the offset from UTC for the user's time zone
        val offset = timeZone.rules.getOffset(currentDateTime)
        // Convert to timestamp in milliseconds
        val currentTimestamp = currentDateTime.toEpochSecond(offset) * 1000
        return currentTimestamp.toString()
    }

    suspend fun showNewMessages(list: ArrayList<Message>) {
        cacheNewMessages(list)
    }

    suspend fun cacheNewMessages(list: ArrayList<Message>) {
        if (list.isEmpty()) return
        for (message in list) {
            repository.db.myDao().insert(message)
        }
        try {
            pagingSource.invalidate()
        } catch (_: Exception) {
        }
    }

    suspend fun cacheNewMessageSent(message: Message) {
        CoroutineScope(Dispatchers.IO).launch {
            if (checkIfMessageISNotCachedInLastPage(message)) {
                repository.insertMessageToDatabase(message)
                pagingSource.invalidate()
            }
        }
    }

    //return true if message is not cached
    private suspend fun checkIfMessageISNotCachedInLastPage(message: Message): Boolean {
        return !pagingAdapterList.any { it == message }
    }

    suspend fun getCachedMessages(room: Room): ArrayList<Message> {
        return repository.db.myDao().getMessagesByRoomID(room.roomId) as ArrayList<Message>
    }

    suspend fun updateRoomBackground(thisRoom: Room) {
        roomsRepository.updateRoom(thisRoom, true)
    }

    suspend fun changeRoomBackgroundColor(thisRoom: Room): Room {
        var x = thisRoom.roomBackgroundColor
        x++
        if (x > 7) x = 0
        thisRoom.roomBackgroundColor = x
        return thisRoom
    }

}