// --- ForgotPasswordScreen.kt ---

package com.example.alertify.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val primaryRed = Color(0xFFF44336)
    var input by remember { mutableStateOf("") }
    var showCodeField by remember { mutableStateOf(false) }
    var confirmationCode by remember { mutableStateOf("") }
    val generatedCode = "123456" // Simulation

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mot de passe oublié", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Email ou numéro de téléphone") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (!showCodeField) {
            Button(
                onClick = { showCodeField = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(primaryRed)
            ) {
                Text("Envoyer le code")
            }
        } else {
            Text("Code envoyé ! (simulé : $generatedCode)", color = primaryRed)
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = confirmationCode,
                onValueChange = { confirmationCode = it },
                label = { Text("Code de confirmation") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (confirmationCode == generatedCode) {
                        navController.navigate("reset_password")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(primaryRed)
            ) {
                Text("Valider le code")
            }
        }
    }
}
