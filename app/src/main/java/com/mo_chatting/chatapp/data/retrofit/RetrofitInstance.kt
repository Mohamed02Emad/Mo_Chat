package com.mo_chatting.chatapp.data.retrofit

import com.mo_chatting.chatapp.appClasses.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

companion object{

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

     val apiService by lazy {
        retrofit.create(NotificationApi::class.java)
    }

}


}