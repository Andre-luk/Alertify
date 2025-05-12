package com.example.alertify.screens.admin

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.alertify.models.AdminIncident
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminIncidentsScreen(navController: NavController) {
    var incidents by remember { mutableStateOf<List<AdminIncident>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Charger les incidents à partir de Firestore
    LaunchedEffect(Unit) {
        getIncidentsFromFirestore { result, error ->
            if (error != null) {
                errorMessage = error
            } else {
                incidents = result ?: emptyList()
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestion des Incidents") },
                actions = {
                    IconButton(onClick = { /* Ajouter un incident */ }) {
                        Icon(Icons.Filled.Add, contentDescription = "Ajouter un Incident")
                    }
                }
            )
        },
        content = { padding ->
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                errorMessage?.let {
                    Text(
                        text = "Erreur: $it",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    items(incidents.size) { index ->
                        val incident = incidents[index]
                        IncidentItem(incident) {
                            // Action pour afficher ou modifier les détails de l'incident
                        }
                    }
                }
            }
        }
    )
}

// Fonction pour récupérer les incidents depuis Firestore
private fun getIncidentsFromFirestore(onComplete: (List<AdminIncident>?, String?) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("incidents")
        .get()
        .addOnSuccessListener { querySnapshot ->
            val incidentsList = querySnapshot.toAdminIncidentList()
            onComplete(incidentsList, null)
        }
        .addOnFailureListener { exception ->
            Log.e("AdminIncidentsScreen", "Erreur lors de la récupération des incidents", exception)
            onComplete(null, "Erreur lors de la récupération des incidents.")
        }
}

// Extension pour convertir QuerySnapshot en liste d'AdminIncident
private fun QuerySnapshot.toAdminIncidentList(): List<AdminIncident> {
    return this.documents.map { documentSnapshot ->
        documentSnapshot.toObject(AdminIncident::class.java)?.copy(id = documentSnapshot.id) ?: AdminIncident()
    }
}

// Composant pour afficher chaque incident
@Composable
fun IncidentItem(incident: AdminIncident, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Type : ${incident.type}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Description : ${incident.description}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Statut : ${incident.status}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Créé le : ${incident.createdAt}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
