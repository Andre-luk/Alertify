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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.navigation.NavController
import java.util.*

data class User(val id: String, var name: String, var email: String, var role: String)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(navController: NavController) {
    val primaryRed = Color(0xFFF44336)

    var users by remember {
        mutableStateOf(
            listOf(
                User(UUID.randomUUID().toString(), "Jean Dupont", "jean@mail.com", "utilisateur"),
                User(UUID.randomUUID().toString(), "Sophie Leroy", "sophie@mail.com", "superuser")
            )
        )
    }

    var showDialog by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<User?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestion des utilisateurs", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryRed)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingUser = null
                    showDialog = true
                },
                containerColor = primaryRed
            ) {
                Text("+", color = Color.White, style = MaterialTheme.typography.headlineSmall)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(users) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFDECEA))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nom : ${user.name}")
                        Text("Email : ${user.email}")
                        Text("Rôle : ${user.role}")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = {
                                editingUser = user
                                showDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = primaryRed)
                            }

                            IconButton(onClick = {
                                users = users.filterNot { it.id == user.id }
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
        UserDialog(
            user = editingUser,
            onDismiss = { showDialog = false },
            onSave = { newUser ->
                users = if (editingUser == null) {
                    users + newUser.copy(id = UUID.randomUUID().toString())
                } else {
                    users.map {
                        if (it.id == editingUser!!.id) newUser else it
                    }
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun UserDialog(user: User?, onDismiss: () -> Unit, onSave: (User) -> Unit) {
    var name by remember { mutableStateOf(TextFieldValue(user?.name ?: "")) }
    var email by remember { mutableStateOf(TextFieldValue(user?.email ?: "")) }
    var role by remember { mutableStateOf(user?.role ?: "utilisateur") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (name.text.isNotBlank() && email.text.isNotBlank()) {
                    onSave(User(user?.id ?: "", name.text, email.text, role))
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
        title = { Text(if (user == null) "Ajouter un utilisateur" else "Modifier l’utilisateur") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nom") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                Spacer(modifier = Modifier.height(8.dp))
                Text("Rôle :")
                Row {
                    listOf("utilisateur", "superuser").forEach { r ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { role = r }
                        ) {
                            RadioButton(selected = role == r, onClick = { role = r })
                            Text(r)
                        }
                    }
                }
            }
        }
    )
}
