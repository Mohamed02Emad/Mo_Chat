package com.mo_chatting.chatapp.validation

import java.util.regex.Matcher
import java.util.regex.Pattern


fun isValidEmail(email: CharSequence): ValidationResault {
    if (email.isBlank()) return ValidationResault(false,"email field is empty")
    var isValid = true
    var message = "is valid"
    val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(email)
    if (!matcher.matches()) {
        isValid = false
        message = "invalid email pattern"
    }
    return ValidationResault(isValid, message = message )
}

fun validatePassword(password: String): ValidationResault {
    if (password.length < 8) return ValidationResault(false, "too short password")
    if (password.length > 20) return ValidationResault(false, " too long password")
    var u = 0
    var l = 0
    var d = 0
    for (char in password) {
        if (char.isUpperCase()) u++
        else if (char.isLowerCase()) l++
        else if (char.isDigit()) d++
    }
    if (u == 0) return ValidationResault(false, "no capital letter")
    if (l == 0) return ValidationResault(false, "no lowerCase letter")
    if (d == 0) return ValidationResault(false, "no digits")

    return ValidationResault(true, "is valid")
}

fun validateUserName(userName:String):ValidationResault{
    if (userName.isBlank())return ValidationResault(false,"name is empty")
    if (userName.length==1)return ValidationResault(false,"name is too short")
    if (userName.length>30)return ValidationResault(false,"name is too long")
    return ValidationResault(true,"is valid")
}

class ValidationResault(val isValid: Boolean, val message: String)