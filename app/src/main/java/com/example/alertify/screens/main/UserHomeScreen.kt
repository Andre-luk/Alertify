package com.example.alertify.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.alertify.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(navController: NavController, isUserLoggedIn: Boolean, userName: String) {
    var showMenu by remember { mutableStateOf(false) }
    var showAlertTypes by remember { mutableStateOf(false) }
    var showReportForm by remember { mutableStateOf(false) }
    var selectedAlertType by remember { mutableStateOf("") }

    val alertTypes = listOf(
        "Vol", "Agressions", "Accident", "Infrastructure dangereuse", "Incendie", "Catastrophe naturelle", "Personne disparue"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Logo à gauche
                        Image(
                            painter = painterResource(id = R.drawable.ic_logo), // Remplace par le logo de ton app
                            contentDescription = "Logo",
                            modifier = Modifier.size(40.dp)
                        )

                        // Icône du menu utilisateur à droite
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Menu utilisateur")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ExtendedFloatingActionButton(
                    onClick = { showAlertTypes = true },
                    icon = { Icon(Icons.Default.Warning, "Alert") },
                    text = { Text("Signaler") }
                )
            }
        }

        // Affichage du menu déroulant des alertes
        DropdownMenu(expanded = showAlertTypes, onDismissRequest = { showAlertTypes = false }) {
            alertTypes.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                        selectedAlertType = type
                        showAlertTypes = false
                        showReportForm = true
                    }
                )
            }
        }

        // Menu utilisateur
        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
            DropdownMenuItem(text = { Text("Mon compte") }, onClick = { /* Naviguer vers mon compte */ })
            DropdownMenuItem(text = { Text("Mes alertes") }, onClick = { /* Naviguer vers mes alertes */ })
            DropdownMenuItem(text = { Text("Déconnexion") }, onClick = { /* Gérer la déconnexion */ })
        }

        // Affichage du formulaire de signalement
        if (showReportForm) {
            ReportForm(alertType = selectedAlertType, isUserLoggedIn = isUserLoggedIn, onDismiss = { showReportForm = false })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportForm(alertType: String, isUserLoggedIn: Boolean, onDismiss: () -> Unit) {
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Signaler un Incident") },
        text = {
            Column {
                Text(text = "Type de signalement: $alertType", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (!isUserLoggedIn) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lieu") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Soumettre")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Annuler")
            }
        }
    )
}
