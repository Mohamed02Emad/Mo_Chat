package com.mo_chatting.chatapp.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private var _email = MutableLiveData<String>("")
     val email : LiveData<String> = _email

    private var _password = MutableLiveData<String>("")
     val password : LiveData<String> = _password

    fun setEmail(email:String) {
        _email.value = email
    }
    fun setPassword(password:String){
        _password.value=password
    }
}