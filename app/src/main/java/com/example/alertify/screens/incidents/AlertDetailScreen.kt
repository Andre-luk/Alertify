package com.example.alertify.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDetailScreen(navController: NavController, alertId: String) {
    val db = FirebaseFirestore.getInstance()

    var alertData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(alertId) {
        isLoading = true
        db.collection("incidents").document(alertId).get()
            .addOnSuccessListener { doc ->
                alertData = doc.data
                isLoading = false
            }
            .addOnFailureListener { e ->
                errorMessage = e.localizedMessage
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détails de l'alerte") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage != null -> {
                    Text(
                        text = "Erreur : $errorMessage",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                alertData == null -> {
                    Text(
                        text = "Aucune donnée disponible.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    val type = alertData?.get("type") as? String ?: "-"
                    val urgency = alertData?.get("urgency") as? String ?: "-"
                    val status = alertData?.get("status") as? String ?: "-"
                    val description = alertData?.get("description") as? String ?: "-"
                    val timestamp = alertData?.get("createdAt") as? Timestamp
                    val dateStr = timestamp?.toDate()?.let {
                        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(it)
                    } ?: "-"

                    Column {
                        Text("Type : $type", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Urgence : $urgency", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Statut : $status", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Date : $dateStr", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Description :", style = MaterialTheme.typography.titleMedium)
                        Text(description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
