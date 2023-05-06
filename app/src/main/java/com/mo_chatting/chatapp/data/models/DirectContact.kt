package com.mo_chatting.chatapp.data.models

import retrofit2.http.Url

data class DirectContact (
    var name : String? = null,
    // equal to roomId for caching messages
    val token : String? = null,
    var imageUrl : String?=null,
    var lastMessage : String? = null,
        )