package com.example.alertify.screens.admin

import androidx.compose.foundation.clickable
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

data class Incident(
    val id: String,
    var title: String,
    var type: String,
    var service: String
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminIncidentsScreen(navController: NavController) {
    val primaryRed = Color(0xFFF44336)

    var incidents by remember {
        mutableStateOf(
            listOf(
                Incident(UUID.randomUUID().toString(), "Fuite de gaz", "Accident", "Pompiers"),
                Incident(UUID.randomUUID().toString(), "Vol à l'étalage", "Incident", "Police")
            )
        )
    }

    var editingIncident by remember { mutableStateOf<Incident?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestion des incidents", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryRed)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingIncident = null
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
            items(incidents) { incident ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Titre : ${incident.title}")
                        Text("Type : ${incident.type}")
                        Text("Service associé : ${incident.service}")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = {
                                editingIncident = incident
                                showDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = primaryRed)
                            }

                            IconButton(onClick = {
                                incidents = incidents.filterNot { it.id == incident.id }
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
        IncidentDialog(
            incident = editingIncident,
            onDismiss = { showDialog = false },
            onSave = { newIncident ->
                incidents = if (editingIncident == null) {
                    incidents + newIncident.copy(id = UUID.randomUUID().toString())
                } else {
                    incidents.map {
                        if (it.id == editingIncident!!.id) newIncident else it
                    }
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun IncidentDialog(incident: Incident?, onDismiss: () -> Unit, onSave: (Incident) -> Unit) {
    var title by remember { mutableStateOf(incident?.title ?: "") }
    var type by remember { mutableStateOf(incident?.type ?: "Incident") }
    var service by remember { mutableStateOf(incident?.service ?: "Police") }

    val types = listOf("Incident", "Accident", "Autre")
    val services = listOf("Police", "Pompiers", "Hôpital")

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (title.isNotBlank()) {
                    onSave(Incident(incident?.id ?: "", title, type, service))
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
        title = { Text(if (incident == null) "Ajouter un incident" else "Modifier l'incident") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titre") })
                Spacer(modifier = Modifier.height(8.dp))

                Text("Type :")
                types.forEach {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable { type = it }
                    ) {
                        RadioButton(selected = type == it, onClick = { type = it })
                        Text(it)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Service associé :")
                services.forEach {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable { service = it }
                    ) {
                        RadioButton(selected = service == it, onClick = { service = it })
                        Text(it)
                    }
                }
            }
        }
    )
}
