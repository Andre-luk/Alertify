package com.example.alertify.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.*

data class Service(
    val id: String,
    var name: String
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminServicesScreen(navController: NavController) {
    val primaryRed = Color(0xFFF44336)

    var services by remember {
        mutableStateOf(
            listOf(
                Service(UUID.randomUUID().toString(), "Police"),
                Service(UUID.randomUUID().toString(), "Pompiers"),
                Service(UUID.randomUUID().toString(), "Hôpital")
            )
        )
    }

    var editingService by remember { mutableStateOf<Service?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestion des services", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryRed)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingService = null
                showDialog = true
            }, containerColor = primaryRed) {
                Text("+", color = Color.White, style = MaterialTheme.typography.headlineSmall)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(services) { service ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(service.name)

                        Row {
                            IconButton(onClick = {
                                editingService = service
                                showDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = primaryRed)
                            }

                            IconButton(onClick = {
                                services = services.filterNot { it.id == service.id }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        ServiceDialog(
            service = editingService,
            onDismiss = { showDialog = false },
            onSave = { newService ->
                services = if (editingService == null) {
                    services + newService.copy(id = UUID.randomUUID().toString())
                } else {
                    services.map {
                        if (it.id == editingService!!.id) newService else it
                    }
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun ServiceDialog(service: Service?, onDismiss: () -> Unit, onSave: (Service) -> Unit) {
    var name by remember { mutableStateOf(service?.name ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) {
                    onSave(Service(service?.id ?: "", name))
                }
            }) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        },
        title = { Text(if (service == null) "Ajouter un service" else "Modifier le service") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom du service") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}
