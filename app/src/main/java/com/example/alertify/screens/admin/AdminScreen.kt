package com.example.alertify.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AdminScreen(navController: NavController) {
    // Variables d'état pour la gestion des incidents
    var selectedIncident by remember { mutableStateOf("") }
    var assignedService by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Interface Administrateur",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Champ pour saisir l'ID de l'incident
        OutlinedTextField(
            value = selectedIncident,
            onValueChange = { selectedIncident = it },
            label = { Text("ID Incident") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Champ pour saisir le service auquel attribuer l'incident
        OutlinedTextField(
            value = assignedService,
            onValueChange = { assignedService = it },
            label = { Text("Attribuer au Service (ex: Police)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Bouton pour attribuer l'incident
        Button(
            onClick = {
                // Ajouter ici le traitement d'attribution (mise à jour de la base, appel API, etc.)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Attribuer Incident")
        }
    }
}
