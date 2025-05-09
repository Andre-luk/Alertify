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

data class AdminUser(
    val id: String,
    var email: String,
    var role: String
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAdminsScreen(navController: NavController) {
    val primaryRed = Color(0xFFF44336)

    var admins by remember {
        mutableStateOf(
            listOf(
                AdminUser(UUID.randomUUID().toString(), "admin1@example.com", "superadmin"),
                AdminUser(UUID.randomUUID().toString(), "admin2@example.com", "modérateur")
            )
        )
    }

    val currentAdminId = remember { admins.first().id } // Simulation : l'admin connecté est le premier

    var editingAdmin by remember { mutableStateOf<AdminUser?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestion des administrateurs", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryRed)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingAdmin = null
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
            items(admins) { admin ->
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
                        Column {
                            Text(admin.email)
                            Text("Rôle : ${admin.role}", style = MaterialTheme.typography.bodySmall)
                        }

                        Row {
                            IconButton(onClick = {
                                editingAdmin = admin
                                showDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = primaryRed)
                            }

                            IconButton(
                                onClick = {
                                    if (admin.id != currentAdminId) {
                                        admins = admins.filterNot { it.id == admin.id }
                                    }
                                },
                                enabled = admin.id != currentAdminId
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Supprimer",
                                    tint = if (admin.id != currentAdminId) Color.Gray else Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AdminDialog(
            admin = editingAdmin,
            onDismiss = { showDialog = false },
            onSave = { updatedAdmin ->
                admins = if (editingAdmin == null) {
                    admins + updatedAdmin.copy(id = UUID.randomUUID().toString())
                } else {
                    admins.map {
                        if (it.id == editingAdmin!!.id) updatedAdmin else it
                    }
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun AdminDialog(admin: AdminUser?, onDismiss: () -> Unit, onSave: (AdminUser) -> Unit) {
    var email by remember { mutableStateOf(admin?.email ?: "") }
    var role by remember { mutableStateOf(admin?.role ?: "modérateur") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (email.isNotBlank()) {
                    onSave(AdminUser(admin?.id ?: "", email, role))
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
        title = { Text(if (admin == null) "Ajouter un admin" else "Modifier l'admin") },
        text = {
            Column {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Rôle") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
