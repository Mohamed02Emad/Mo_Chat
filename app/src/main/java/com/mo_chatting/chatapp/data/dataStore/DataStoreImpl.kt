package com.mo_chatting.chatapp.data.dataStore

import android.content.Context
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
    }

    override suspend fun clearAll() {
        mDataStore.edit { it.clear() }
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
}