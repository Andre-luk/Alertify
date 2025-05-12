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

    // State variables
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
    var showError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isSubmitted by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val sexeOptions = listOf("M", "F")
    var selectedSexe by remember { mutableStateOf(sexeOptions.first()) }

    // DatePicker setup
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

    // Save user in Firestore
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
                error = "Erreur lors de l'enregistrement dans Firestore."
            }
    }

    // Registration logic
    fun register() {
        if (prenom.isBlank() || nom.isBlank() || email.isBlank()
            || password.isBlank() || password != confirmPassword
            || dateNaissance.isBlank()
        ) {
            showError = true
            return
        }

        showError = false
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
                    error = task.exception?.localizedMessage ?: "Erreur lors de l'inscription"
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

        AnimatedVisibility(visible = showError) {
            Text("Veuillez remplir tous les champs obligatoires.", color = Color.Red)
        }
        AnimatedVisibility(visible = error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        // Identity Card
        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFBE9E7))
        ) {
            Column(Modifier.padding(12.dp)) {
                Text("Identité", fontSize = 18.sp, color = primaryRed)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = prenom,
                        onValueChange = { prenom = it },
                        label = { Text("Prénom *") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = nom,
                        onValueChange = { nom = it },
                        label = { Text("Nom *") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text("Sexe", modifier = Modifier.align(Alignment.Start))
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
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = dateNaissance,
                    onValueChange = { },
                    label = { Text("Date de naissance *") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Choisir une date",
                            modifier = Modifier.clickable { datePickerDialog.show() }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() }
                )
            }
        }

        // Contact Card
        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))
        ) {
            Column(Modifier.padding(12.dp)) {
                Text("Contact", fontSize = 18.sp, color = primaryRed)
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email *") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Téléphone (optionnel)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
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

        // Security Card
        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA))
        ) {
            Column(Modifier.padding(12.dp)) {
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
                                contentDescription = null
                            )
                        }
                    },
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
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Loading & Submit
        AnimatedVisibility(visible = isLoading) {
            CircularProgressIndicator(color = primaryRed)
        }
        AnimatedVisibility(visible = !isLoading && !isSubmitted) {
            Button(
                onClick = { register() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = primaryRed)
            ) {
                Text("S'inscrire")
            }
        }
        AnimatedVisibility(visible = isSubmitted, enter = fadeIn(), exit = fadeOut()) {
            Text("Inscription réussie !", color = Color.Green, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Déjà un compte ? Connectez-vous",
            color = primaryRed,
            modifier = Modifier.clickable { navController.navigate("login") }
        )
    }
}
