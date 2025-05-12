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
import com.example.alertify.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TransformedText  // Import de TransformedText

// Classe de transformation du mot de passe
class CustomPasswordVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Remplacer chaque caractère par un astérisque
        val transformed = "*".repeat(text.length)

        // Retourner la transformation visuelle et un OffsetMapping correct
        return TransformedText(
            AnnotatedString(transformed),
            OffsetMapping.Identity  // Utilise l'offset d'identité, car il n'y a pas de transformation sur l'index
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRegisterScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    fun registerAdmin() {
        if (password != confirmPassword) {
            errorMessage = "Les mots de passe ne correspondent pas."
            return
        }

        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Tous les champs doivent être remplis."
            return
        }

        isLoading = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Créer un utilisateur dans Firestore avec le rôle "admin"
                    val user = auth.currentUser
                    val userData = hashMapOf(
                        "email" to user?.email,
                        "role" to "admin",
                        "name" to "Admin"  // Vous pouvez personnaliser le nom
                    )

                    user?.let {
                        db.collection("users").document(it.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(navController.context, "Inscription réussie", Toast.LENGTH_SHORT).show()
                                navController.navigate("admin_dashboard") {
                                    popUpTo("admin_register") { inclusive = true }
                                }
                            }
                            .addOnFailureListener { e ->
                                errorMessage = "Erreur lors de l'inscription dans Firestore."
                                isLoading = false
                            }
                    }
                } else {
                    errorMessage = "Erreur lors de l'inscription : ${task.exception?.message}"
                    isLoading = false
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inscription Admin", color = Color.White) },
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
            Text("Créez un compte administrateur", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            // Champs Email
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // Champs Mot de passe
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe") },
                visualTransformation = CustomPasswordVisualTransformation(),  // Transformation personnalisée
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // Champs Confirmer le mot de passe
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmer le mot de passe") },
                visualTransformation = CustomPasswordVisualTransformation(),  // Transformation personnalisée
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            // Bouton d'inscription
            Button(
                onClick = { registerAdmin() },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("S'inscrire")
                }
            }
        }
    }
}
