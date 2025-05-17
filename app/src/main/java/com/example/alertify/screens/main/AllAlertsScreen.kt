package com.example.alertify.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllAlertsScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var reports by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        db.collection("incidents")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                reports = snapshot.documents.mapNotNull { doc ->
                    doc.data?.toMutableMap()?.apply {
                        put("id", doc.id)
                    }
                }
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
                title = { Text("Toutes les alertes") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                errorMessage != null -> {
                    Text(
                        text = "Erreur : $errorMessage",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                reports.isEmpty() -> {
                    Text(
                        text = "Aucune alerte disponible.",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        items(reports) { report ->
                            AllAlertCard(report = report, onClick = {
                                val id = report["id"] as? String
                                id?.let { navController.navigate("alert_detail/$id") }
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AllAlertCard(report: Map<String, Any>, onClick: () -> Unit) {
    val type = report["type"] as? String ?: "-"
    val urgency = report["urgency"] as? String ?: "-"
    val status = report["status"] as? String ?: "-"
    val timestamp = report["createdAt"] as? Timestamp
    val dateStr = timestamp?.toDate()?.let {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(it)
    } ?: "-"

    val urgencyColor = when (urgency.lowercase(Locale.getDefault())) {
        "élevée" -> Color.Red
        "moyenne" -> Color(0xFFFF9800)
        "faible" -> Color(0xFF4CAF50)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = cardElevation(defaultElevation = 4.dp),
        colors = cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Type : $type", style = MaterialTheme.typography.bodyLarge)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .background(urgencyColor, shape = MaterialTheme.shapes.small)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = urgency,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }

                Text(
                    text = "Statut : $status",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "Créé le : $dateStr",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
