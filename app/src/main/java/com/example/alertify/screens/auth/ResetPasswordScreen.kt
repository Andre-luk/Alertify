package com.example.alertify.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.text.input.VisualTransformation


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(navController: NavController) {
    val red = Color(0xFFF44336)
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Définir un nouveau mot de passe") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = red,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    errorMessage = ""
                    successMessage = ""
                },
                label = { Text("Nouveau mot de passe") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showPassword = !showPassword }) {
                        Text(if (showPassword) "Cacher" else "Voir")
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = ""
                    successMessage = ""
                },
                label = { Text("Confirmer le mot de passe") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    when {
                        newPassword.isBlank() || confirmPassword.isBlank() -> {
                            errorMessage = "Tous les champs sont obligatoires"
                        }
                        newPassword != confirmPassword -> {
                            errorMessage = "Les mots de passe ne correspondent pas"
                        }
                        currentUser == null -> {
                            errorMessage = "Utilisateur non connecté"
                        }
                        else -> {
                            isLoading = true
                            currentUser.updatePassword(newPassword)
                                .addOnSuccessListener {
                                    isLoading = false
                                    successMessage = "Mot de passe réinitialisé avec succès"
                                    auth.signOut()
                                    navController.navigate("login") {
                                        popUpTo("reset_password") { inclusive = true }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    errorMessage = "Erreur : ${e.message}"
                                }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = red),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Réinitialiser")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage.isNotBlank()) {
                Text(errorMessage, color = Color.Red)
            }

            if (successMessage.isNotBlank()) {
                Text(successMessage, color = Color.Green)
            }
        }
    }
}
