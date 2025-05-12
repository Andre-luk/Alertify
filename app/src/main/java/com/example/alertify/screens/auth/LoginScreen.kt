package com.example.alertify.screens.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.alertify.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val redColor = Color(0xFFF44336)
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isLoggedIn by remember { mutableStateOf(false) }
    var showForm by remember { mutableStateOf(true) }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Google Sign-In setup
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    // Launcher for Google Sign-In Intent
    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                account?.let {
                    firebaseAuthWithGoogle(
                        it,
                        auth,
                        coroutineScope,
                        db,
                        navController,
                        context
                    )
                }
            } catch (e: ApiException) {
                error = "Google sign-in failed: ${e.localizedMessage}"
                isLoading = false
                showForm = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = showForm,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Connexion", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.MailOutline, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mot de passe") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (error.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(error, color = Color.Red)
                }

                Spacer(Modifier.height(10.dp))
                Text(
                    "Mot de passe oublié ?",
                    modifier = Modifier.clickable { navController.navigate("forgot_password") },
                    color = redColor
                )
                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        error = ""
                        if (email.isBlank() || password.isBlank()) {
                            error = "Veuillez remplir tous les champs"
                            return@Button
                        }
                        isLoading = true
                        showForm = false
                        auth.signInWithEmailAndPassword(email.trim(), password.trim())
                            .addOnSuccessListener { res ->
                                coroutineScope.launch {
                                    delay(1000)
                                    navigateByRole(res.user, db, navController)
                                }
                            }
                            .addOnFailureListener {
                                error = "Email ou mot de passe incorrect"
                                isLoading = false
                                showForm = true
                            }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = redColor)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Se connecter", color = Color.White)
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text("OU", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        val signInIntent: Intent = googleClient.signInIntent
                        googleLauncher.launch(signInIntent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Se connecter avec Google")
                }

                Spacer(Modifier.height(20.dp))
                Text(
                    "Créer un compte",
                    modifier = Modifier.clickable { navController.navigate("register") },
                    color = redColor
                )
            }
        }

        AnimatedVisibility(
            visible = isLoggedIn,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut()
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Connexion réussie",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

private fun navigateByRole(user: FirebaseUser?, db: FirebaseFirestore, navController: NavController) {
    user?.uid?.let { uid ->
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role") ?: "user"
                val dest = if (role == "admin") "admin_dashboard" else "user_home"
                navController.navigate(dest) { popUpTo("login") { inclusive = true } }
            }
    }
}

private fun firebaseAuthWithGoogle(
    account: GoogleSignInAccount,
    auth: FirebaseAuth,
    coroutineScope: CoroutineScope,
    db: FirebaseFirestore,
    navController: NavController,
    context: Context
) {
    auth.signInWithCredential(GoogleAuthProvider.getCredential(account.idToken, null))
        .addOnSuccessListener { res ->
            coroutineScope.launch {
                delay(1000)
                navigateByRole(res.user, db, navController)
            }
        }
}