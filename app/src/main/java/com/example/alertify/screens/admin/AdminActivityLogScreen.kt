package com.example.alertify.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

data class ActivityLog(
    val id: String,
    val date: Date,
    val user: String,
    val action: String
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminActivityLogScreen(navController: NavController) {
    val primaryRed = Color(0xFFF44336)

    val formatter = remember { SimpleDateFormat("dd MMM yyyy à HH:mm", Locale.FRANCE) }

    val logs by remember {
        mutableStateOf(
            listOf(
                ActivityLog(UUID.randomUUID().toString(), Date(), "admin1@example.com", "Ajout d'un nouvel utilisateur"),
                ActivityLog(UUID.randomUUID().toString(), Date(), "admin2@example.com", "Modification d’un incident"),
                ActivityLog(UUID.randomUUID().toString(), Date(), "admin1@example.com", "Changement de rôle : John → Superuser")
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journal des activités", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryRed)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier
            .padding(padding)
            .padding(16.dp)
        ) {
            items(logs) { log ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = formatter.format(log.date), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(text = log.user, style = MaterialTheme.typography.labelMedium, color = primaryRed)
                        Text(text = log.action, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
