package com.mo_chatting.chatapp.presentation.MainActivity

import android.app.Application
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.mo_chatting.chatapp.appClasses.Constants
import com.mo_chatting.chatapp.data.dataStore.DataStoreImpl
import com.mo_chatting.chatapp.data.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val appContext: Application,
    val firebaseAuth: FirebaseAuth,
    val firebaseFirestore: FirebaseFirestore
) : ViewModel() {

    @Inject
    lateinit var dataStoreImpl: DataStoreImpl

    suspend fun setOnlineState(isOnline: Boolean) {
        try {
            val userId = dataStoreImpl.getUserId()
            val userQuery = getUser(userId!!) ?: return
            val user = userQuery.toObject<User>()
            user.isOnline = isOnline
            val updatedUser = updateUserOnlineState(user)
            val usersRef = firebaseFirestore.collection(Constants.users)
            usersRef.document(userQuery.id).set(user, SetOptions.merge())
        }catch (_:Exception){}
    }

    private suspend fun updateUserOnlineState(user: User): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["isOnline"] = user.isOnline
        return map
    }


    private suspend fun getUser(userId: String): QueryDocumentSnapshot? {
        val usersRef = firebaseFirestore.collection(Constants.users)
        val userQuery = usersRef
            .whereEqualTo("userId", userId)
            .get()
            .await()
        for (user in userQuery) {
            return user
        }
        return null
    }


}