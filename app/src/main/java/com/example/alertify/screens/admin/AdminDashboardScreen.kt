package com.example.alertify.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController) {
    val primaryRed = Color(0xFFF44336)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin - Tableau de bord", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryRed)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DashboardButton("Gestion des utilisateurs", Icons.Default.Person, primaryRed) {
                navController.navigate("admin_users")
            }

            DashboardButton("Gestion des incidents", Icons.Default.ReportProblem, primaryRed) {
                navController.navigate("admin_incidents")
            }

            DashboardButton("Gestion des services", Icons.Default.Build, primaryRed) {
                navController.navigate("admin_services")
            }

            DashboardButton("Gestion des admins", Icons.Default.Security, primaryRed) {
                navController.navigate("admin_manage_admins")
            }

            DashboardButton("Journal des activités", Icons.Default.ListAlt, primaryRed) {
                navController.navigate("admin_logs")
            }
        }
    }
}

@Composable
fun DashboardButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
