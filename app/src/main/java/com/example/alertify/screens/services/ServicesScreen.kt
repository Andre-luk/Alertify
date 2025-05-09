package com.example.alertify.screens.services

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.alertify.model.IncidentPredefined  // Import de l'objet centralisé

@Composable
fun ServicesScreen(navController: NavController) {
    // On génère la liste des services à afficher à partir du mapping par défaut contenu dans IncidentPredefined.
    // L'utilisation de toSet() permet d'éviter les doublons.
    val services = IncidentPredefined.DEFAULT_SERVICE_MAPPING.values.toSet().toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Interface Services",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(services) { service ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            // Action lors du clic sur un service (ex. affichage de détails)
                        }
                ) {
                    Text(
                        text = service,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
