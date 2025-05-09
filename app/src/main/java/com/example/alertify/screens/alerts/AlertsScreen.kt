package com.example.alertify.screens.alerts

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen() {
    var newAlert by remember { mutableStateOf("") }
    val alertList = remember { mutableStateListOf<String>() }
    val context = LocalContext.current
    val primaryRed = Color(0xFFF44336)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes Alertes", color = Color.White) },
                actions = {
                    IconButton(onClick = {
                        alertList.clear()
                        Toast.makeText(context, "Toutes les alertes ont été supprimées", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Supprimer les alertes", tint = Color.White)
                    }
                },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = newAlert,
                onValueChange = { newAlert = it },
                label = { Text("Ajouter une alerte") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFFAFAFA),
                    focusedIndicatorColor = primaryRed,
                    cursorColor = primaryRed
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (newAlert.isNotBlank()) {
                        alertList.add(newAlert)
                        newAlert = ""
                        Toast.makeText(context, "Alerte ajoutée", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Veuillez saisir un message d'alerte", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryRed,
                    contentColor = Color.White
                )
            ) {
                Text("Ajouter Alerte")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (alertList.isEmpty()) {
                Text(
                    "Aucune alerte pour le moment",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(alertList, key = { it.hashCode() }) { alert ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = primaryRed)
                        ) {
                            Text(
                                text = alert,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.White,
                                    fontSize = 16.sp
                                ),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
