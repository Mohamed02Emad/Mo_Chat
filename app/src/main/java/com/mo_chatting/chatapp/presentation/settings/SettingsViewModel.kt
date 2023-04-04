package com.mo_chatting.chatapp.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mo_chatting.chatapp.data.dataStore.DataStoreImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var dataStore:DataStoreImpl

    private val _imageQualitySwitch = MutableLiveData<Boolean?>(false)
    val imageQualitySwitch :LiveData<Boolean?> = _imageQualitySwitch

    private val _darkModeSwitch = MutableLiveData<Boolean?>(false)
    val darkModeSwitch :LiveData<Boolean?> = _darkModeSwitch


    private val _notification = MutableLiveData<Boolean?>(false)
    val notification :LiveData<Boolean?> = _notification

    init {
        CoroutineScope(Dispatchers.Main).launch {
            _imageQualitySwitch.postValue(getLowImageQuality())
            _darkModeSwitch.postValue(getDarkMode())
            _notification.postValue(getNotificationEnabled())
        }
    }


    suspend fun getDarkMode():Boolean = dataStore.getDarkMode()
    suspend fun setDarkMode()=dataStore.setDarkMode(!dataStore.getDarkMode())

    suspend fun getLowImageQuality():Boolean = dataStore.getLowImageQuality()
    suspend fun setLowImageQuality()= dataStore.setLowImageQuality(!dataStore.getLowImageQuality())



    suspend fun getNotificationEnabled():Boolean = dataStore.getNotificationEnabled()
    suspend fun setNotificationEnabled()= dataStore.setNotificationEnabled(!dataStore.getNotificationEnabled())




}