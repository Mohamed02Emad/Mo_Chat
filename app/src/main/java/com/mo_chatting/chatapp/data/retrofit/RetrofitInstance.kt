package com.mo_chatting.chatapp.data.retrofit

import com.mo_chatting.chatapp.BuildConfig
import com.mo_chatting.chatapp.appClasses.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object {

        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val apiService by lazy {
            retrofit.create(NotificationApi::class.java)
        }

        fun getClient(): OkHttpClient {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            return OkHttpClient().newBuilder().apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(interceptor)
                    addInterceptor {
                        it.proceed(it.request())
                    }
                }
            }.build()
        }

    }

}