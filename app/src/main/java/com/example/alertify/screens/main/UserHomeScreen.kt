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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.alertify.R
import com.example.alertify.model.IncidentPredefined
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Color.Companion.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(navController: NavController, isUserLoggedIn: Boolean, userName: String) {
    var showMenu by remember { mutableStateOf(false) }
    var showAlertTypes by remember { mutableStateOf(false) }
    var showReportForm by remember { mutableStateOf(false) }
    var selectedAlertType by remember { mutableStateOf("") }

    // Utilisation de la liste partagée depuis IncidentPredefined
    val alertTypes = IncidentPredefined.INCIDENT_TYPES

    // Animation de clignotement pour le bouton
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
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
                            painter = painterResource(id = R.drawable.logo_alertify),
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
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // 🎨 Image de fond en haut avec ratio 1.5/3 de l'écran
            Image(
                painter = painterResource(id = R.drawable.fondecran), // Remplace par ton image
                contentDescription = "Image de fond",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f) // Prend 50% de la hauteur totale de l'écran
            )

            // 📝 Message professionnel en haut de l'image de fond
            Text(
                text = "Bienvenue, veuillez signaler un incident",
                style = TextStyle(
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize // Remplacé h6 par bodyLarge
                ),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 20.dp)
            )

            // 📢 Nouveau texte pour demander à l'utilisateur de cliquer sur "Commencer"
            Text(
                text = "Cliquez sur 'signaler' pour signaler un incident.",
                style = TextStyle(
                    color = Color(0xFFF44336), // Rouge professionnel
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize // Style ajusté
                ),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 120.dp) // Ajuster la position sous le message principal
            )

            // 🧾 Contenu texte et bouton centré en bas
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ⚠️ Bouton Signaler en rouge professionnel avec clignotement
                ExtendedFloatingActionButton(
                    onClick = { showAlertTypes = true },
                    icon = { Icon(Icons.Default.Warning, contentDescription = "Alert") },
                    text = { Text("Signaler") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer(alpha = alpha), // Applique l'alpha animé pour le clignotement
                    containerColor = Color(0xFFF44336), // Rouge professionnel
                    contentColor = Color.White,
                    // Clignotement du bouton
                    elevation = FloatingActionButtonDefaults.elevation(6.dp, 12.dp)
                )
            }
        }

        // Affichage du menu déroulant des alertes
        DropdownMenu(expanded = showAlertTypes, onDismissRequest = { showAlertTypes = false }) {
            alertTypes.forEach { type ->
                DropdownMenuItem(
//                    color = {Color(0xFFB71C1C)},
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
            DropdownMenuItem(text = { Text("Mon compte") }, onClick = { navController.navigate("account") })
            DropdownMenuItem(text = { Text("Mes alertes") }, onClick = { navController.navigate("alerts") })
            DropdownMenuItem(text = { Text("Déconnexion") }, onClick = {
                // Logique de déconnexion
                navController.popBackStack() // Retire les écrans précédents
                navController.navigate("login") // Redirige vers l'écran de login
            })
        }

        // Affichage du formulaire de signalement
        if (showReportForm) {
            ReportForm(
                alertType = selectedAlertType,
                isUserLoggedIn = isUserLoggedIn,
                onDismiss = { showReportForm = false }
            )
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
                Text(
                    text = "Type de signalement: $alertType",
                    style = MaterialTheme.typography.bodyLarge
                )
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
