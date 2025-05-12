package com.example.alertify.models

import java.util.*

data class AdminIncident(
    val id: String = "",
    val type: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrl: String = "",
    val status: String = "nouveau",
    val createdAt: Date = Date()
)
