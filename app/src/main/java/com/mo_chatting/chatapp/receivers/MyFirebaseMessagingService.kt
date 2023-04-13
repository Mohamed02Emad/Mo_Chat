package com.mo_chatting.chatapp.receivers

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mo_chatting.chatapp.appClasses.showLocalNotification
import com.mo_chatting.chatapp.data.dataStore.DataStoreImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var dataStore:DataStoreImpl
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("mohamed", "message recieved ")
        CoroutineScope(Dispatchers.Main).launch {
          //  if (dataStore.getNotificationEnabled()){
                val title = message.data.get("title")
                val body = message.data.get("body")
                val userName = message.data.get("userName")
                val targetRoomId = message.data.get("targetRoomId")
                showLocalNotification(this@MyFirebaseMessagingService,title,body,userName,targetRoomId)
           // }
        }
    }
}
