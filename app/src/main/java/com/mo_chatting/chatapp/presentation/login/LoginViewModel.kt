package com.mo_chatting.chatapp.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.mo_chatting.chatapp.data.dataStore.DataStoreImpl
import com.mo_chatting.chatapp.data.repositories.RoomsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val firebaseAuth: FirebaseAuth,
    val repository: RoomsRepository
) : ViewModel() {

    private var _email = MutableLiveData<String>("")
    val email: LiveData<String> = _email

    private var _password = MutableLiveData<String>("")
    val password: LiveData<String> = _password

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    suspend fun resetPassword(email: String): Boolean {
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            return true
        } catch (_: Exception) {
        }
        return false
    }

    fun UserIsLoged(): Boolean = firebaseAuth.currentUser != null
    suspend fun loginWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
        registerForUserRooms()
    }

    suspend fun loginWithGoogle(credentials: AuthCredential) {
        firebaseAuth.signInWithCredential(credentials).await()
        registerForUserRooms()
    }

    private suspend fun registerForUserRooms() {
        repository.reSubscribeForAllUserRooms()
    }

}