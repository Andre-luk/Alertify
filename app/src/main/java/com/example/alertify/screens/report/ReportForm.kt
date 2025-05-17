package com.example.alertify.screens.report

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

data class Incident(
    val id: String = UUID.randomUUID().toString(),
    val type: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val urgency: String = "Moyenne",
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentScreen() {
    val modernRed = Color(0xFFD32F2F)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var incidents by remember { mutableStateOf(listOf<Incident>()) }
    var showReportForm by remember { mutableStateOf(false) }
    var incidentToEdit by remember { mutableStateOf<Incident?>(null) }

    var userRole by remember { mutableStateOf<String?>(null) }

    // Charger le rôle de l'utilisateur
    LaunchedEffect(auth.currentUser?.uid) {
        auth.currentUser?.uid?.let { uid ->
            try {
                val snapshot = firestore.collection("users").document(uid).get().await()
                userRole = snapshot.getString("role") ?: "citoyen"
            } catch (e: Exception) {
                userRole = "citoyen"
            }
        }
    }

    // Ecoute en temps réel de la collection incidents
    DisposableEffect(Unit) {
        val listenerRegistration: ListenerRegistration = firestore.collection("incidents")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) return@addSnapshotListener
                if (snapshots != null) {
                    incidents = snapshots.documents.mapNotNull { doc ->
                        doc.toObject(Incident::class.java)?.copy(id = doc.id)
                    }
                }
            }
        onDispose { listenerRegistration.remove() }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Liste des incidents",
                        color = modernRed,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    incidentToEdit = null // création nouveau
                    showReportForm = true
                },
                containerColor = modernRed,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un incident")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (incidents.isEmpty()) {
                item {
                    Text(
                        "Aucun incident signalé.",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            items(incidents) { incident ->
                IncidentCard(
                    incident = incident,
                    modernRed = modernRed,
                    userRole = userRole ?: "citoyen",
                    onEdit = {
                        incidentToEdit = it
                        showReportForm = true
                    },
                    onDelete = { toDelete ->
                        scope.launch {
                            try {
                                firestore.collection("incidents").document(toDelete.id).delete().await()
                                snackbarHostState.showSnackbar("Incident supprimé")
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Erreur suppression: ${e.message}")
                            }
                        }
                    }
                )
            }
        }

        if (showReportForm) {
            ReportForm(
                incidentToEdit = incidentToEdit,
                onDismiss = { showReportForm = false },
                onSubmit = { newIncident ->
                    scope.launch {
                        try {
                            if (incidentToEdit != null) {
                                firestore.collection("incidents").document(newIncident.id).set(newIncident).await()
                                snackbarHostState.showSnackbar("Incident mis à jour")
                            } else {
                                firestore.collection("incidents").document(newIncident.id).set(newIncident).await()
                                snackbarHostState.showSnackbar("Signalement envoyé")
                            }
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar("Erreur: ${e.message}")
                        }
                        showReportForm = false
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentCard(
    incident: Incident,
    modernRed: Color,
    userRole: String,
    onEdit: (Incident) -> Unit,
    onDelete: (Incident) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, modernRed.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    incident.type,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = modernRed,
                    modifier = Modifier.weight(1f)
                )

                if (userRole == "admin" || userRole == "agent") {
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Modifier") },
                                onClick = {
                                    expanded = false
                                    onEdit(incident)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Supprimer", color = Color.Red) },
                                onClick = {
                                    expanded = false
                                    onDelete(incident)
                                }
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(incident.description, maxLines = 3, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            incident.imageUrl?.let { url ->
                Image(
                    painter = rememberAsyncImagePainter(url),
                    contentDescription = "Image incident",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(8.dp))
            }
            Text(
                "Urgence: ${incident.urgency}",
                style = MaterialTheme.typography.bodySmall,
                color = modernRed
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportForm(
    incidentToEdit: Incident? = null,
    onDismiss: () -> Unit,
    onSubmit: (Incident) -> Unit
) {
    val modernRed = Color(0xFFD32F2F)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val alertTypes = listOf("Incendie", "Accident", "Inondation", "Vol")
    var selectedAlertType by remember { mutableStateOf(incidentToEdit?.type ?: alertTypes.first()) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    var description by remember { mutableStateOf(incidentToEdit?.description ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var urgencyLevel by remember { mutableStateOf(incidentToEdit?.urgency ?: "Moyenne") }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
    }

    val storage = FirebaseStorage.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (incidentToEdit == null) "Nouveau Signalement" else "Modifier Signalement",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = modernRed
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Type d'alerte", style = MaterialTheme.typography.bodySmall)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0xFFFAFAFA), shape = RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { dropdownExpanded = true }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(selectedAlertType, modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = if (dropdownExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        alertTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedAlertType = type
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )

                Text("Urgence", style = MaterialTheme.typography.bodySmall)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("Faible", "Moyenne", "Élevée").forEach { level ->
                        FilterChip(
                            selected = urgencyLevel == level,
                            onClick = { urgencyLevel = level },
                            label = { Text(level) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = modernRed.copy(alpha = 0.2f),
                                selectedLabelColor = modernRed
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Image sélectionnée",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, modernRed)
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Choisir une photo")
                }
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    if (description.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Veuillez saisir une description.")
                        }
                        return@FilledTonalButton
                    }
                    isSubmitting = true

                    scope.launch {
                        try {
                            var uploadedImageUrl: String? = incidentToEdit?.imageUrl
                            if (imageUri != null) {
                                val filename = "incidents/${UUID.randomUUID()}.jpg"
                                val ref = storage.reference.child(filename)
                                ref.putFile(imageUri!!).await()
                                uploadedImageUrl = ref.downloadUrl.await().toString()
                            }

                            val currentUser = auth.currentUser
                            if (currentUser == null) {
                                snackbarHostState.showSnackbar("Utilisateur non connecté")
                                isSubmitting = false
                                return@launch
                            }

                            val newIncident = Incident(
                                id = incidentToEdit?.id ?: UUID.randomUUID().toString(),
                                type = selectedAlertType,
                                description = description,
                                imageUrl = uploadedImageUrl,
                                urgency = urgencyLevel,
                                timestamp = incidentToEdit?.timestamp ?: System.currentTimeMillis(),
                                userId = currentUser.uid
                            )
                            onSubmit(newIncident)
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar("Erreur lors de l'envoi : ${e.message}")
                        } finally {
                            isSubmitting = false
                        }
                    }
                },
                enabled = !isSubmitting
            ) {
                Text(if (incidentToEdit == null) "Envoyer" else "Mettre à jour")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )

    // Snackbar Host
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        SnackbarHost(hostState = snackbarHostState)
    }
}
