package com.mo_chatting.chatapp.appClasses

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mo_chatting.chatapp.MainActivity
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.data.models.Room

private fun showNotification(
    context: Context,
    roomName: String?,
    message: String?,
    room: Room,
) {

    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra("FRAGMENT_TO_OPEN",room.roomName )
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
            .setContentTitle(roomName)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val name: CharSequence = "Main Notification Channel"
    val importance = NotificationManager.IMPORTANCE_HIGH
    val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
    notificationManager.createNotificationChannel(mChannel)
    notificationManager.notify(
        room.roomId.toInt(16),
        notificationBuilder.build()
    )
}