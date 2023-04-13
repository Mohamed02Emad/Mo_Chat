package com.mo_chatting.chatapp.receivers

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mo_chatting.chatapp.appClasses.mapNotificationData
import com.mo_chatting.chatapp.appClasses.showLocalNotification


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("mohamed", "message received ")

        val data = mapNotificationData(message.data)
        val title = data.title
        val body = data.body
        val userName = data.userName
        val targetRoomId = data.roomId
        Log.d("mohamed", "title: $title, body: $body, targetRoomId: $targetRoomId")
        showLocalNotification(
            this@MyFirebaseMessagingService,
            title,
            body,
            userName,
            targetRoomId
        )
    }

    override fun onMessageSent(msgId: String) {
        super.onMessageSent(msgId)
        Log.d("mohamed", "onMessageSent: $msgId")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("mohamed", "onNewToken: $token")
    }

}
