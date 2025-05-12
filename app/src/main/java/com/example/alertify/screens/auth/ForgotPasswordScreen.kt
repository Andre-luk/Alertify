package com.example.alertify.screens.auth

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val redColor = Color(0xFFF44336)
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var input by remember { mutableStateOf("") }
    var isPhoneNumber by remember { mutableStateOf(false) }
    var confirmationCode by remember { mutableStateOf("") }
    var showCodeField by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    var storedVerificationId by remember { mutableStateOf<String?>(null) }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Auto-retrieval or instant validation
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.navigate("reset_password")
                } else {
                    error = "Erreur de vérification automatique"
                }
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            error = "Échec de vérification : ${e.message}"
            Log.e("PhoneAuth", "Verification Failed", e)
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            storedVerificationId = verificationId
            resendToken = token
            showCodeField = true
        }
    }

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
            onValueChange = {
                input = it
                isPhoneNumber = it.matches(Regex("^\\+?[0-9]{10,15}$")) // format téléphone
                error = ""
            },
            label = { Text("Email ou numéro de téléphone") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (!showCodeField) {
            Button(
                onClick = {
                    error = ""
                    if (input.isBlank()) {
                        error = "Champ requis"
                        return@Button
                    }

                    if (isPhoneNumber) {
                        val options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(input)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(context as Activity)
                            .setCallbacks(callbacks)
                            .build()
                        PhoneAuthProvider.verifyPhoneNumber(options)
                    } else {
                        auth.fetchSignInMethodsForEmail(input).addOnSuccessListener { result ->
                            if (result.signInMethods?.isNotEmpty() == true) {
                                auth.sendPasswordResetEmail(input)
                                    .addOnSuccessListener {
                                        error = "Email de réinitialisation envoyé"
                                    }
                                    .addOnFailureListener {
                                        error = "Erreur d'envoi de l'email"
                                    }
                            } else {
                                error = "Email non reconnu"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(redColor)
            ) {
                Text("Envoyer le code")
            }
        }

        if (showCodeField && isPhoneNumber) {
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = confirmationCode,
                onValueChange = { confirmationCode = it },
                label = { Text("Code de confirmation SMS") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val verificationId = storedVerificationId
                    if (verificationId != null) {
                        val credential = PhoneAuthProvider.getCredential(verificationId, confirmationCode)
                        auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    navController.navigate("reset_password")
                                } else {
                                    error = "Code incorrect ou expiré"
                                }
                            }
                    } else {
                        error = "Identifiant de vérification manquant"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(redColor)
            ) {
                Text("Valider le code")
            }
        }

        if (error.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(error, color = Color.Red)
        }
    }
}
