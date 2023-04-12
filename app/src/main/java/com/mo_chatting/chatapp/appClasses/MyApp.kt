package com.mo_chatting.chatapp.appClasses

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.mo_chatting.chatapp.data.dataStore.DataStoreImpl
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application(){
    @Inject
    lateinit var dataStoreImpl: DataStoreImpl

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        CoroutineScope(Dispatchers.Main).launch {
            if (dataStoreImpl.getDarkMode()){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}