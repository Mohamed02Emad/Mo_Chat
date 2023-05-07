package com.mo_chatting.chatapp.data.dataStore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


private val Context.dataStore by preferencesDataStore("user_data")

class DataStoreImpl(
    appContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DataStore {

    private val mDataStore by lazy {
        appContext.dataStore
    }

    companion object {
        const val USER_NAME = "userName"
        const val USER_IMAGE = "userImage"
        const val IMAGE_IS_LOW = "lowQualityImages"
        const val DARK_MODE = "darkMode"
        const val NOTIFICATIONS = "notifications"
        const val USER = "user"
    }

    override suspend fun clearAll() {
        val mode = getDarkMode()
        mDataStore.edit { it.clear() }
        setDarkMode(mode)
    }

    override suspend fun getUserImage(): String = withContext(dispatcher) {
        mDataStore.data.map { settings ->
            settings[stringPreferencesKey(USER_IMAGE)] ?: "null"
        }.first()
    }

    override suspend fun getUserName(): String = withContext(dispatcher) {
        mDataStore.data.map { settings ->
            settings[stringPreferencesKey(USER_NAME)] ?: "null"
        }.first()

    }

    override suspend fun setUserImage(imgLocation: String) {
        withContext(dispatcher) {
            mDataStore.edit { settings ->
                settings[stringPreferencesKey(USER_IMAGE)] = imgLocation
            }
        }
    }

    override suspend fun setUserName(userName: String) {
        withContext(dispatcher) {
            mDataStore.edit { settings ->
                settings[stringPreferencesKey(USER_NAME)] = userName
            }
        }
    }

    override suspend fun saveUserId(userId: String) {
        withContext(dispatcher) {
            mDataStore.edit { settings ->
                settings[stringPreferencesKey(USER)] = userId
            }
        }
    }


    override suspend fun getUserId(): String = withContext(dispatcher) {
        mDataStore.data.map { settings ->
            settings[stringPreferencesKey(USER)] ?: "null"
        }.first()
    }

    override suspend fun setLowImageQuality(isLow: Boolean) {
        withContext(dispatcher) {
            mDataStore.edit { settings ->
                settings[booleanPreferencesKey(IMAGE_IS_LOW)] = isLow
            }
        }
    }

    override suspend fun getLowImageQuality(): Boolean = withContext(dispatcher) {
        mDataStore.data.map { settings ->
            settings[booleanPreferencesKey(IMAGE_IS_LOW)] ?: true
        }.first()

    }

    override suspend fun getDarkMode(): Boolean = withContext(dispatcher) {
        mDataStore.data.map { settings ->
            settings[booleanPreferencesKey(DARK_MODE)] ?: true
        }.first()
    }

    override suspend fun setDarkMode(isLow: Boolean) {
        withContext(dispatcher) {
            mDataStore.edit { settings ->
                settings[booleanPreferencesKey(DARK_MODE)] = isLow
            }
        }
    }

    override suspend fun getNotificationEnabled(): Boolean = withContext(dispatcher) {
        mDataStore.data.map { settings ->
            settings[booleanPreferencesKey(NOTIFICATIONS)] ?: true
        }.first()
    }

    override suspend fun setNotificationEnabled(enable: Boolean) {
        withContext(dispatcher) {
            mDataStore.edit { settings ->
                settings[booleanPreferencesKey(NOTIFICATIONS)] = enable
            }
        }
    }


}