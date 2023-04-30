package com.mo_chatting.chatapp.services

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mo_chatting.chatapp.appClasses.Constants.isOnline
import com.mo_chatting.chatapp.appClasses.mapNotificationData
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
    lateinit var dataStore: DataStoreImpl
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = mapNotificationData(message.data)
        val title = data.title
        val body = data.body
        val userName = data.userName
        val targetRoomId = data.roomId
        val owner = data.ownerId
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        CoroutineScope(Dispatchers.IO).launch {
            if (userId != owner && dataStore.getNotificationEnabled() && !isOnline) {
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

    override fun onMessageSent(msgId: String) {
        super.onMessageSent(msgId)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

}
