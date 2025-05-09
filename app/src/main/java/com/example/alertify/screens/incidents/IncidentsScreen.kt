package com.example.alertify.screens.incidents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.alertify.model.IncidentPredefined

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentsScreen(navController: NavController, defaultIncidentType: String) {
    // Mapping entre le type d'incident et le service par défaut
    val defaultServiceMapping = mapOf(
        "Vol" to "Police",
        "Agressions" to "Police",
        "Accident" to "Mairie",
        "Infrastructure dangereuse" to "Gouvernement",
        "Incendie" to "Pompiers",
        "Catastrophe naturelle" to "Gouvernement",
        "Personne disparue" to "Police"
    )

    // Pré-remplissage du champ Service en fonction du type d'incident
    var idService by remember { mutableStateOf(defaultServiceMapping[defaultIncidentType] ?: "") }
    var incidentType by remember { mutableStateOf(defaultIncidentType) }
    var customIncidentDescription by remember { mutableStateOf("") }
    var incidentDate by remember { mutableStateOf("") }
    var incidentLocation by remember { mutableStateOf("") }
    var incidentDetails by remember { mutableStateOf("") }

    // Utilisation de la liste partagée pour les types d'incidents
    val incidentTypes = IncidentPredefined.INCIDENT_TYPES

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Interface Incidents",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Champ affichant le service assigné (pré-rempli)
        OutlinedTextField(
            value = idService,
            onValueChange = { idService = it },
            label = { Text("Service assigné") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Menu déroulant pour choisir/modifier le type d'incident
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = incidentType,
                onValueChange = { },
                readOnly = true,
                label = { Text("Type d'incident") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                incidentTypes.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            incidentType = selectionOption
                            // Met à jour le service par défaut en fonction du choix
                            idService = defaultServiceMapping[selectionOption] ?: ""
                            expanded = false
                        }
                    )
                }
            }
        }

        // Si le type choisi est "Autre", afficher un champ de description complémentaire
        if (incidentType == "Autre") {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = customIncidentDescription,
                onValueChange = { customIncidentDescription = it },
                label = { Text("Description de l'incident") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Champ pour la date de l'incident
        OutlinedTextField(
            value = incidentDate,
            onValueChange = { incidentDate = it },
            label = { Text("Date (ex: 2025-04-08)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Champ pour le lieu de l'incident
        OutlinedTextField(
            value = incidentLocation,
            onValueChange = { incidentLocation = it },
            label = { Text("Lieu") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Champ pour les détails supplémentaires du lieu
        OutlinedTextField(
            value = incidentDetails,
            onValueChange = { incidentDetails = it },
            label = { Text("Détails du lieu") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Bouton de sauvegarde/traitement de l'incident
        Button(
            onClick = {
                // Ajouter ici le traitement (ex. appel API, sauvegarde locale, etc.)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enregistrer Incident")
        }
    }
}
