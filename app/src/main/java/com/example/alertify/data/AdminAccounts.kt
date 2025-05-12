package com.alertify.data

object AdminAccounts {
    private val admins = listOf(
        Admin(email = "andrelukengu@alertify.com", password = "admin123"),
        Admin(email = "manasselotafe@alertify.com", password = "admin456")
    )

    fun isAdmin(email: String, password: String): Boolean {
        return admins.any { it.email == email && it.password == password }
    }
}

data class Admin(val email: String, val password: String)
