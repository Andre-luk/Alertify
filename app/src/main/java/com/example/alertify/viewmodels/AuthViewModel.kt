package com.example.alertify.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    private val _isUserLoggedIn = mutableStateOf(false)
    val isUserLoggedIn: State<Boolean> = _isUserLoggedIn

    private val _userName = mutableStateOf("")
    val userName: State<String> = _userName

    fun login(name: String) {
        _isUserLoggedIn.value = true
        _userName.value = name
    }

    fun logout() {
        _isUserLoggedIn.value = false
        _userName.value = ""
    }
}
