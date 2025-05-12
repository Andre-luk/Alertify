package com.example.alertify.screens.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAccessScreen(navController: NavController) {
    var secretCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val validSecretCode = "ADMIN_SECRET" // Le code secret que seul l'admin connaît

    // Fonction pour vérifier le code d'accès
    fun checkAccess() {
        if (secretCode == validSecretCode) {
            navController.navigate("admin_register") // Redirection vers l'écran d'inscription
        } else {
            errorMessage = "Code d'accès invalide" // Affichage du message d'erreur
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accès Admin", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF44336))
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Entrez le code d'accès administrateur", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            // Champ pour entrer le code secret
            TextField(
                value = secretCode,
                onValueChange = { secretCode = it },
                label = { Text("Code d'accès") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            // Bouton de validation
            Button(
                onClick = { checkAccess() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Accéder à l'inscription")
            }
        }
    }
}
