package com.mo_chatting.chatapp.data.models

import android.graphics.Bitmap

class User (
    var userName:String,
    var userImageUri:String,
    var userId:String,
    var lastSeen:String,
    var isOnline:Boolean = false,
        )