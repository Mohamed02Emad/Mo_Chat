package com.mo_chatting.chatapp.presentation.dialogs.searchUser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mo_chatting.chatapp.data.dataStore.DataStoreImpl
import com.mo_chatting.chatapp.data.models.User
import com.mo_chatting.chatapp.data.repositories.SearchUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchUserViewModel @Inject constructor(private val repository: SearchUserRepository) :
    ViewModel() {
    @Inject
    lateinit var dataStore: DataStoreImpl
    private val _users = MutableLiveData<ArrayList<User>>(ArrayList())
    val users: LiveData<ArrayList<User>> = _users

    suspend fun getUsersById(userId: String) {
        val searchResult = repository.getUsersWithId(userId)
        val currentUserId = dataStore.getUserId()!!
        val currentUser = repository.getUser(currentUserId)
        for (user in searchResult) {
            if (user.userId == currentUserId || currentUser!!.friends.contains(user.userId)) {
                searchResult.remove(user)
            }
        }
        setUsers(searchResult)
    }

    private fun setUsers(users: ArrayList<User>) {
        _users.value!!.clear()
        _users.postValue(users)
    }

    suspend fun addUserToFriends(user: User) {
        val currentUserId = dataStore.getUserId()
        if (currentUserId != null) {
            repository.addUserToFriends(currentUserId, user)
        }
    }

    suspend fun removeUserFromFriends(user: User) {
        val currentUserId = dataStore.getUserId()
        if (currentUserId != null) {
            repository.removeUserFromFriends(currentUserId, user.userId, user.token)
        }
    }

    fun resetList() {
        _users.value!!.clear()
    }
}