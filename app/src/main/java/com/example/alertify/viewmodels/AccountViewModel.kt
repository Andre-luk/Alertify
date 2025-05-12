package com.example.alertify.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AccountViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail

    init {
        val user = auth.currentUser
        _userName.value = user?.displayName ?: "Nom inconnu"
        _userEmail.value = user?.email ?: "Email inconnu"
    }

    fun logout() {
        auth.signOut()
    }
}
