package com.example.alertify.screens.auth

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val primaryRed = Color(0xFFF44336)
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    // États des champs
    var nom by remember { mutableStateOf("") }
    var prenom by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var dateNaissance by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isSubmitted by remember { mutableStateOf(false) }

    val sexeOptions = listOf("M", "F")
    var selectedSexe by remember { mutableStateOf(sexeOptions.first()) }

    // DatePicker dialog
    val calendar = Calendar.getInstance()
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            calendar.set(year, month, day)
            dateNaissance = dateFormatter.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Validation en temps réel (exemples simples)
    val isPrenomValid = prenom.isNotBlank()
    val isNomValid = nom.isNotBlank()
    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordValid = password.length >= 6
    val isConfirmPasswordValid = confirmPassword == password && confirmPassword.isNotBlank()
    val isDateNaissanceValid = dateNaissance.isNotBlank()

    fun saveUserData(userId: String) {
        val userMap = mapOf(
            "prenom" to prenom,
            "nom" to nom,
            "email" to email,
            "phone" to phone,
            "location" to location,
            "dateNaissance" to dateNaissance,
            "sexe" to selectedSexe,
            "role" to "user"
        )
        db.collection("users").document(userId)
            .set(userMap)
            .addOnFailureListener {
                errorMessage = "Erreur lors de l'enregistrement dans Firestore."
            }
    }

    fun register() {
        if (!isPrenomValid || !isNomValid || !isEmailValid || !isPasswordValid
            || !isConfirmPasswordValid || !isDateNaissanceValid
        ) {
            errorMessage = "Veuillez remplir correctement tous les champs obligatoires."
            return
        }

        errorMessage = ""
        isLoading = true

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    saveUserData(uid)
                    isSubmitted = true
                    navController.navigate("user_home") {
                        popUpTo("register") { inclusive = true }
                    }
                } else {
                    errorMessage = task.exception?.localizedMessage ?: "Erreur lors de l'inscription"
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Créer un compte", style = MaterialTheme.typography.headlineMedium)

        AnimatedVisibility(visible = errorMessage.isNotEmpty()) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxWidth())
        }

        // Section Identité
        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFBE9E7))
        ) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Identité", fontSize = 18.sp, color = primaryRed)

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = prenom,
                        onValueChange = { prenom = it },
                        label = { Text("Prénom *") },
                        modifier = Modifier.weight(1f),
                        isError = !isPrenomValid && prenom.isNotEmpty(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                    OutlinedTextField(
                        value = nom,
                        onValueChange = { nom = it },
                        label = { Text("Nom *") },
                        modifier = Modifier.weight(1f),
                        isError = !isNomValid && nom.isNotEmpty(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                }

                Text("Sexe", modifier = Modifier.padding(top = 4.dp))
                Row {
                    sexeOptions.forEach { sexe ->
                        Row(
                            Modifier
                                .weight(1f)
                                .clickable { selectedSexe = sexe },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedSexe == sexe,
                                onClick = { selectedSexe = sexe },
                                colors = RadioButtonDefaults.colors(selectedColor = primaryRed)
                            )
                            Text(sexe)
                        }
                    }
                }

                OutlinedTextField(
                    value = dateNaissance,
                    onValueChange = {},
                    label = { Text("Date de naissance *") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Choisir une date de naissance",
                            modifier = Modifier.clickable { datePickerDialog.show() }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    isError = !isDateNaissanceValid && dateNaissance.isNotEmpty()
                )
            }
        }

        // Section Contact
        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))
        ) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Contact", fontSize = 18.sp, color = primaryRed)

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = !isEmailValid && email.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Téléphone (optionnel)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Localisation (optionnelle)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Section Sécurité
        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA))
        ) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Sécurité", fontSize = 18.sp, color = primaryRed)

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mot de passe *") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Cacher le mot de passe" else "Afficher le mot de passe"
                            )
                        }
                    },
                    isError = !isPasswordValid && password.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmer le mot de passe *") },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Cacher la confirmation" else "Afficher la confirmation"
                            )
                        }
                    },
                    isError = !isConfirmPasswordValid && confirmPassword.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { register() },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = primaryRed)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text("S'inscrire", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Déjà un compte ? Connexion", color = primaryRed)
        }
    }
}
