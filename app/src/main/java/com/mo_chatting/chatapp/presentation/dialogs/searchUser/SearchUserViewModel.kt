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
        val currentUserId = dataStore.getUserId()
        for (user in searchResult){
            if (user.userId == currentUserId){
                searchResult.remove(user)
                break
            }
        }
            setUsers(searchResult)
    }

    private fun setUsers(users: ArrayList<User>) {
        _users.value!!.clear()
        _users.postValue(users)
    }

    fun addUserToFriends(user: User) {
        //TODO("Not yet implemented")
    }
}