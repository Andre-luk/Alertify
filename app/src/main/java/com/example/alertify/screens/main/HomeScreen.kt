package com.example.alertify.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
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
fun HomeScreen(navController: NavController) {
    var showAlertTypes by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showReportForm by remember { mutableStateOf(false) }
    var selectedAlertType by remember { mutableStateOf("") }

    val alertTypes = listOf(
        "Vol",
        "Agressions",
        "Accident",
        "Infrastructure dangereuse"
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
                        Image(
                            painter = painterResource(R.drawable.ic_logo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(40.dp)
                        )
                        Text("Accueil", style = MaterialTheme.typography.titleLarge)
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
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

            DropdownMenu(
                expanded = showAlertTypes,
                onDismissRequest = { showAlertTypes = false }
            ) {
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

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Connexion") },
                    onClick = {
                        showMenu = false
                        navController.navigate("login")
                    }
                )
            }

            if (showReportForm) {
                ReportForm(
                    alertType = selectedAlertType,
                    onDismiss = { showReportForm = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportForm(alertType: String, onDismiss: () -> Unit) {
    var date by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

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

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mot de passe") },
                    modifier = Modifier.fillMaxWidth()
                )

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
            TextButton(onClick = {
                // Handle report submission
                onDismiss()
            }) {
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
