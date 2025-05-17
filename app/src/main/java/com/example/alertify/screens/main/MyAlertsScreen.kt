package com.example.alertify.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.alertify.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAlertsScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var reports by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uid) {
        if (uid != null) {
            isLoading = true
            db.collection("incidents")
                .whereEqualTo("userId", uid)
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
        } else {
            errorMessage = "Utilisateur non connecté"
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes alertes", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                errorMessage != null -> {
                    Text(
                        text = "Erreur : $errorMessage",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                reports.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_report), // assure-toi d’avoir une icône
                            contentDescription = "Alerte vide",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Aucune alerte trouvée.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(reports) { report ->
                            AlertCard(report = report, onClick = {
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
fun AlertCard(report: Map<String, Any>, onClick: () -> Unit) {
    val type = report["type"] as? String ?: "-"
    val urgency = report["urgency"] as? String ?: "-"
    val status = report["status"] as? String ?: "-"
    val timestamp = report["createdAt"] as? Timestamp
    val dateStr = timestamp?.toDate()?.let {
        SimpleDateFormat("dd MMM yyyy à HH:mm", Locale.getDefault()).format(it)
    } ?: "-"

    val urgencyColor = when (urgency.lowercase(Locale.getDefault())) {
        "élevée" -> Color(0xFFD32F2F)
        "moyenne" -> Color(0xFFFFA000)
        "faible" -> Color(0xFF388E3C)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = cardElevation(defaultElevation = 3.dp),
        colors = cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = type,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Urgence : $urgency",
                color = urgencyColor,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Statut : $status",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Créé le : $dateStr",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
