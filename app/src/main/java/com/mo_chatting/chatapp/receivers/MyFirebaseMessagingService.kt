package com.mo_chatting.chatapp.receivers

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mo_chatting.chatapp.appClasses.MyApp
import com.mo_chatting.chatapp.appClasses.showLocalNotification
import com.mo_chatting.chatapp.data.dataStore.DataStoreImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("mohamed", "message received ")
        CoroutineScope(Dispatchers.IO).launch {
            val title = "test title"
            val body = "test body"
            val userName = "mohamed emad"
            val targetRoomId = "123456"
            showLocalNotification(
                this@MyFirebaseMessagingService,
                title,
                body,
                userName,
                targetRoomId
            )
        }
    }

}
