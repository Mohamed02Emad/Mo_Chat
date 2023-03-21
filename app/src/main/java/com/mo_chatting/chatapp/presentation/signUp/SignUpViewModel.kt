package com.mo_chatting.chatapp.presentation.signUp

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {
    var userName = ""
    var email = ""
    var passwrod = ""
    var confirmPassword = ""
}