package com.mo_chatting.chatapp.data.retrofit

import com.mo_chatting.chatapp.appClasses.Constants.CONTENT_TYPE
import com.mo_chatting.chatapp.appClasses.Constants.SERVER_KEY
import com.mo_chatting.chatapp.data.models.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {
    @Headers("Authorization: Key=$SERVER_KEY","Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun sendNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}