package com.mo_chatting.chatapp.data.dataStore

interface DataStore {

    suspend fun getUserImage():String
    suspend fun setUserImage(imgLocation:String)

    suspend fun getUserName():String
    suspend fun setUserName(userName:String)

    suspend fun getLowImageQuality():Boolean
    suspend fun setLowImageQuality(isLow:Boolean)
    suspend fun getDarkMode():Boolean
    suspend fun setDarkMode(isLow:Boolean)
    suspend fun clearAll()

}
