package com.mo_chatting.chatapp.data.repositories

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.mo_chatting.chatapp.appClasses.Constants
import com.mo_chatting.chatapp.data.models.DirectContact
import com.mo_chatting.chatapp.data.models.Message
import com.mo_chatting.chatapp.data.models.User
import kotlinx.coroutines.tasks.await

class SearchUserRepository(
    private val firebaseFireStore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val appContext: Context
) {

    private val usersRef = firebaseFireStore.collection(Constants.users)
    private val directChatsRef = firebaseFireStore.collection(Constants.directChatCollection)

    suspend fun addUserToFriends(currentUserId: String, newUser: User) {
        val currentUser = getUser(currentUserId) ?: return
        currentUser.friends.add(newUser.userId)
        newUser.friends.add(currentUser.userId)
        val updatedUser = mapUser(currentUser)
        val updatedUser2 = mapUser(newUser)
        updateUser(updatedUser, currentUserId)
        updateUser(updatedUser2, newUser.userId)
        createDirectChatForUsers(
            currentUser.token,
            newUser.token,
            currentUserId,
            newUser.userId,
            currentUser.userName,
            newUser.userName,
            currentUser.imageUrl,
            newUser.imageUrl
        )
    }

    suspend fun removeUserFromFriends(
        currentUserId: String,
        newUserId: String,
        newUserToken: String
    ) {
        val currentUser = getUser(currentUserId) ?: return
        currentUser.friends.remove(newUserId)
        val updatedUser = mapUser(currentUser)
        updateUser(updatedUser, currentUserId)
        deleteDirectChatForUser(currentUser.token, newUserToken)
    }

    suspend fun getUsersWithId(userId: String): ArrayList<User> {
        val userQueryById = usersRef
            .whereGreaterThanOrEqualTo("userId", userId)
            .whereLessThan("userId", userId + "\uf8ff")
            .get()
            .await()

        val userQueryByName = usersRef
            .whereGreaterThanOrEqualTo("userName", userId)
            .whereLessThan("userName", userId + "\uf8ff")
            .get()
            .await()
        val listToReturn = ArrayList<User>()
        for (user in userQueryById) {
            listToReturn.add(user.toObject<User>())
        }
        for (user in userQueryByName) {
            val currentUser = user.toObject<User>()
            if (!listToReturn.contains(currentUser)) {
                listToReturn.add(currentUser)
            }
        }
        Log.d("mohamed", "getUsersWithId: "+listToReturn.size)
        return listToReturn
    }

    suspend fun getUser(userId: String): User? {
        val userQuery = usersRef
            .whereEqualTo("userId", userId)
            .get()
            .await()
        for (user in userQuery) {
            return user.toObject<User>()
        }
        return null
    }

    private suspend fun createDirectChatForUsers(
        currentUserToken: String,
        newUserToken: String,
        currentUserId: String,
        newUserId: String,
        currentUserName: String,
        newUserName: String,
        currentUserimageUrl: String,
        newUserimageUrl: String
    ) {
        var max: String
        var min: String
        if (currentUserToken > newUserToken) {
            max = currentUserToken
            min = newUserToken
        } else {
            max = newUserToken
            min = currentUserToken
        }
        val users = ArrayList<String>()
        users.add(currentUserId)
        users.add(newUserId)
        val directChat = DirectContact(
            min + max,
            users,
            false,
            currentUserName,
            newUserName,
            currentUserimageUrl,
            newUserimageUrl
        )
        directChatsRef.add(directChat).await()
        createInitialMessage(directChat.roomId)
        joinChatNotifications(directChat.roomId)
    }

    private suspend fun createInitialMessage(roomId: String) {
        val msgRef =
            firebaseFireStore.collection("Chats/${Constants.directChatCollection}/${roomId}")

        msgRef.add(
            Message(
                timeWithMillis = System.currentTimeMillis().toString(),
                messageOwner = "Mo Chat",
                messageOwnerId = "firebase",
                messageRoom = roomId,
                messageText = firebaseAuth.currentUser!!.displayName.toString() + " wants to talk with you ",
                messageDateAndTime = "--/--/----  --:--"
            )
        ).await()

    }

    private suspend fun deleteDirectChatForUser(
        currentUserToken: String,
        newUserToken: String,
    ) {
        var max: String
        var min: String
        if (currentUserToken > newUserToken) {
            max = currentUserToken
            min = newUserToken
        } else {
            max = newUserToken
            min = currentUserToken
        }
        val chatToken = min + max
        val chats = directChatsRef.whereEqualTo("chatToken", chatToken).get().await()
        for (chat in chats) {
            val directChat = directChatsRef.document(chat.id)
            val obj = chat.toObject<DirectContact>()
            leaveChatNotifications(obj.roomId)
            directChat.delete().await()
        }
    }

    private fun mapUser(user: User): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["userName"] = user.userName
        map["userId"] = user.userId
        map["token"] = user.token
        map["userAbout"] = user.userAbout
        map["imageUrl"] = user.imageUrl
        map["friends"] = user.friends
        return map
    }

    private suspend fun updateUser(user: Map<String, Any>, userId: String) {
        try {
            val userQuery = usersRef
                .whereEqualTo("userId", userId)
                .get()
                .await()
            if (userQuery.documents.isNotEmpty()) {
                for (document in userQuery) {
                    usersRef.document(document.id).set(user, SetOptions.merge())
                }
            }
        } catch (_: Exception) {
        }
    }


    private fun joinChatNotifications(token: String) {
        Firebase.messaging.subscribeToTopic(token)
            .addOnCompleteListener { task ->
            }
    }

    private fun leaveChatNotifications(token: String) {
        Firebase.messaging.unsubscribeFromTopic(token)
            .addOnSuccessListener { task ->
            }
    }
}