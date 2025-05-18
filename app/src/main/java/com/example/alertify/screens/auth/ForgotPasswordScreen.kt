package com.example.alertify.screens.auth

import android.app.Activity
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
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

    // États
    var input by remember { mutableStateOf("") }
    var isPhoneNumber by remember { mutableStateOf(false) }
    var confirmationCode by remember { mutableStateOf("") }
    var showCodeField by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var canResend by remember { mutableStateOf(true) }

    var storedVerificationId by remember { mutableStateOf<String?>(null) }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }

    val codeFocusRequester = remember { FocusRequester() }

    LaunchedEffect(showCodeField) {
        if (showCodeField) {
            codeFocusRequester.requestFocus()
        }
    }

    // Callbacks pour la vérification du téléphone
    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.navigate("reset_password")
                } else {
                    error = "Erreur de vérification automatique"
                }
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            isLoading = false
            error = "Échec de vérification : ${e.message}"
            Log.e("PhoneAuth", "Verification Failed", e)
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            isLoading = false
            storedVerificationId = verificationId
            resendToken = token
            showCodeField = true
            canResend = false
            successMessage = "Code envoyé. Vérifiez votre téléphone."
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
        Text("J’ai oublié mon mot de passe", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = input,
            onValueChange = {
                input = it
                isPhoneNumber = it.matches(Regex("^\\+?[0-9]{10,15}$"))
                error = ""
                successMessage = ""
            },
            label = { Text("Email ou numéro de téléphone") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (!showCodeField) {
            Button(
                onClick = {
                    if (input.isBlank()) {
                        error = "Champ requis"
                        return@Button
                    }
                    isLoading = true
                    error = ""
                    successMessage = ""

                    if (isPhoneNumber) {
                        val options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(input)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(context as Activity)
                            .setCallbacks(callbacks)
                            .build()
                        PhoneAuthProvider.verifyPhoneNumber(options)
                    } else {
                        auth.fetchSignInMethodsForEmail(input)
                            .addOnSuccessListener { result ->
                                isLoading = false
                                if (result.signInMethods?.isNotEmpty() == true) {
                                    auth.sendPasswordResetEmail(input)
                                        .addOnSuccessListener {
                                            successMessage = "Email de réinitialisation envoyé"
                                        }
                                        .addOnFailureListener {
                                            error = "Erreur d'envoi : ${it.message}"
                                        }
                                } else {
                                    error = "Email non reconnu"
                                }
                            }
                            .addOnFailureListener {
                                isLoading = false
                                error = "Erreur : ${it.message}"
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = redColor),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Envoyer le code")
            }
        }

        AnimatedVisibility(visible = showCodeField && isPhoneNumber) {
            Column {
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = confirmationCode,
                    onValueChange = { confirmationCode = it },
                    label = { Text("Code de confirmation") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(codeFocusRequester)
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
                    colors = ButtonDefaults.buttonColors(containerColor = redColor)
                ) {
                    Text("Valider le code")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (error.isNotBlank()) {
            Text(error, color = Color.Red)
        }

        if (successMessage.isNotBlank()) {
            Text(successMessage, color = Color.Green)
        }
    }
}

