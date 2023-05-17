package com.mo_chatting.chatapp.data.repositories

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.mo_chatting.chatapp.appClasses.Constants
import com.mo_chatting.chatapp.data.models.User
import kotlinx.coroutines.tasks.await

class SearchUserRepository(
    private val firebaseFireStore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val appContext: Context
) {

    private val usersRef = firebaseFireStore.collection(Constants.users)


    suspend fun getUsersWithId(userId: String): ArrayList<User> {
        val  userQuery = usersRef
            .whereGreaterThanOrEqualTo("userId", userId)
            .whereLessThan("userId", userId + "\uf8ff")
            .get()
            .await()
         val listToReturn = ArrayList<User>()
        for (user in userQuery){
            listToReturn.add(user.toObject<User>())
        }
        return listToReturn
    }

    fun addUserToFriends(userId: String){

    }

    fun removeUserFromFriends(userId: String){

    }
}