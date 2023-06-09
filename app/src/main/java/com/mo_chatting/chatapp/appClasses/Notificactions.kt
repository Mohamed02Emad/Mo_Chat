package com.mo_chatting.chatapp.appClasses

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.data.models.PushNotification
import com.mo_chatting.chatapp.data.retrofit.RetrofitInstance
import com.mo_chatting.chatapp.presentation.MainActivity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun showLocalNotification(
    context: Context,
    title: String?,
    body: String?,
    userName: String?,
    roomId: String?,
) {
    val isDirectChat = roomId!!.length > 8
    var notiTitle: String? = null
    var notiBody: String? = null
    if (isDirectChat){
        notiTitle = userName
        notiBody = body
    }else{
        notiTitle = title
        notiBody = "$userName : $body"
    }
    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra("roomId", roomId)
    val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)
    } else {
        PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )
    }
    val CHANNEL_ID = "Rooms Chanel"
    val notificationBuilder: NotificationCompat.Builder =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_message_24)
            .setContentTitle(notiTitle)
            .setContentText(notiBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val name: CharSequence = "Main Notification Channel"
    val importance = NotificationManager.IMPORTANCE_HIGH
    val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
    notificationManager.createNotificationChannel(mChannel)
    val notificationId = hexToDecimal(roomId!!)
    notificationManager.notify(
        notificationId,
        notificationBuilder.build()
    )
}

fun sendFireBaseNotification(notification: PushNotification) =
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance
                .apiService
                .sendNotification(notification)

            if (response.isSuccessful) {
            } else {
            }
        } catch (e: Exception) {
        }
    }