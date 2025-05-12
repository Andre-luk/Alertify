package com.example.alertify.screens.admin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController) {
    val redColor = Color(0xFFF44336)
    val auth = FirebaseAuth.getInstance()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Admin", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = redColor),
                actions = {
                    IconButton(onClick = {
                        auth.signOut()
                        navController.navigate("login") {
                            popUpTo("admin_dashboard") { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Déconnexion",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NavigationCard(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    title = "Alertes",
                    icon = Icons.Default.Notifications,
                    onClick = { navController.navigate("alertes") },
                    borderColor = redColor
                )
                NavigationCard(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    title = "Utilisateurs",
                    icon = Icons.Default.Person,
                    onClick = { navController.navigate("utilisateurs") },
                    borderColor = redColor
                )
                NavigationCard(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    title = "ReportForm",
                    icon = Icons.Default.Description,
                    onClick = { navController.navigate("reportform") },
                    borderColor = redColor
                )
                NavigationCard(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    title = "Tableau de bord",
                    icon = Icons.Default.Dashboard,
                    onClick = { navController.navigate("tableau_de_bord") },
                    borderColor = redColor
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavigationCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    borderColor: Color
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(icon, contentDescription = title, modifier = Modifier.size(32.dp), tint = borderColor)
                Spacer(modifier = Modifier.height(8.dp))
                Text(title, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
