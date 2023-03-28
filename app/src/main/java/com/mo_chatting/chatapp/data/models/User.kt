package com.mo_chatting.chatapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class User(
    var userName: String,
    var userImageUri: String,
    var userId: String,
    var userAbout: String = " hi there ^_- ",
    var lastSeen: String = "--/-/----  --:--",
    var isOnline: Boolean = false,
    var showOnlineStateToOthers: Boolean = true,
    var showLastSeenToOthers: Boolean = true

) : Parcelable