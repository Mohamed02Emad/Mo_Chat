package com.mo_chatting.chatapp.data.dataStore

interface DataStore {

    suspend fun getUserImage():String?
    suspend fun setUserImage(imgLocation:String?)

    suspend fun getUserName():String?
    suspend fun setUserName(userName:String?)

    suspend fun clearAll()

}
