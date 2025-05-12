package com.example.alertify.screens.account

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alertify.viewmodels.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: AccountViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val primaryRed = Color(0xFFF44336)

    val userName by viewModel.userName.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mon Compte", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryRed
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Informations du compte",
                style = MaterialTheme.typography.headlineSmall,
                color = primaryRed
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Nom : $userName", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp))
            Text("Email : $userEmail", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp))

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { /* Ajoute la logique de modification ici */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryRed,
                    contentColor = Color.White
                )
            ) {
                Text("Modifier le Profil")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryRed,
                    contentColor = Color.White
                )
            ) {
                Text("Se déconnecter")
            }
        }
    }
}
