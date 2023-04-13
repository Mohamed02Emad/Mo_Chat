package com.mo_chatting.chatapp.appClasses

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.mo_chatting.chatapp.data.models.NotificationData
import java.util.*

fun drawableToBitmap(drawable: Drawable): Bitmap? {
    if (drawable is BitmapDrawable) {
        return drawable.bitmap
    }

    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

fun hexToDecimal(hex: String): Int {
    var hex = hex
    var decimal = 0
    val digits = "0123456789ABCDEF"
    hex = hex.uppercase(Locale.getDefault())
    for (i in 0 until hex.length) {
        val c = hex[i]
        val digit = digits.indexOf(c)
        decimal = 16 * decimal + digit
    }
    return decimal
}

fun mapNotificationData(map: Map<String, String>): NotificationData {
    val title = map["title"] ?: ""
    val body = map["body"] ?: ""
    val userName = map["userName"] ?: ""
    val roomId = map["roomId"] ?: ""
    return NotificationData(title, body, userName, roomId )
}
