package com.example.alertify.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.alertify.R
import com.example.alertify.model.IncidentPredefined

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var showAlertTypes by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showReportForm by remember { mutableStateOf(false) }
    var selectedAlertType by remember { mutableStateOf("") }

    val alertTypes = IncidentPredefined.INCIDENT_TYPES

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Accueil",
                            color = Color(0xFFB71C1C),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(R.drawable.logo_alertify),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(70.dp)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        navController.navigate("drawing")
                                    }
                                )
                            }
                    )
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Image de fond
            Image(
                painter = painterResource(id = R.drawable.fondecran),
                contentDescription = "Fond d'écran",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.5f)
            )

            // Bouton "Signaler"
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ExtendedFloatingActionButton(
                        onClick = { showAlertTypes = true },
                        icon = {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "Signaler un incident"
                            )
                        },
                        text = { Text("Signaler") },
                        containerColor = Color(0xFFB71C1C),
                        contentColor = Color.White
                    )
                }
            }

            // Dropdown pour les types d'incidents
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

            // Menu déroulant
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
                DropdownMenuItem(
                    text = { Text("Paramètres") },
                    onClick = {
                        showMenu = false
                        navController.navigate("settings")
                    }
                )
                DropdownMenuItem(
                    text = { Text("À propos") },
                    onClick = {
                        showMenu = false
                        navController.navigate("account")
                    }
                )
            }

            // Formulaire de signalement
            if (showReportForm) {
                ReportForm(
                    alertType = selectedAlertType,
                    navController = navController,
                    onDismiss = { showReportForm = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportForm(
    alertType: String,
    navController: NavController,
    onDismiss: () -> Unit
) {
    val primaryRed = Color(0xFFF44336)

    var date by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    val redTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = primaryRed,
        unfocusedBorderColor = primaryRed,
        focusedLabelColor = primaryRed,
        cursorColor = primaryRed
    )

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                "Signaler un Incident",
                style = MaterialTheme.typography.titleLarge,
                color = primaryRed
            )
        },
        text = {
            Column {
                Text(
                    text = "Type de signalement : $alertType",
                    style = MaterialTheme.typography.bodyLarge,
                    color = primaryRed
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = redTextFieldColors
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email de contact") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = redTextFieldColors
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description de l'incident") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    colors = redTextFieldColors
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lieu") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = redTextFieldColors
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    navController.navigate("incidents/$alertType")
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(contentColor = primaryRed)
            ) {
                Text("Soumettre")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() },
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
            ) {
                Text("Annuler")
            }
        }
    )
}
