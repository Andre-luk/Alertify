package com.example.alertify.screens.report

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Image as ImageIcon
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportForm(
    onDismiss: () -> Unit,
) {
    val modernRed = Color(0xFFD32F2F)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Types d'alerte disponibles
    val alertTypes = listOf("Incendie", "Accident", "Inondation", "Vol")
    var selectedAlertType by remember { mutableStateOf(alertTypes.first()) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    // Champs du formulaire
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var urgencyLevel by remember { mutableStateOf("Moyenne") }

    // Lanceur de sélection d'image
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Nouveau Signalement",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = modernRed
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sélecteur de type d'alerte
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
                            imageVector = if (dropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = modernRed
                        )
                    }
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth()
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

                // Champ de description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description de l'incident") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = modernRed,
                        cursorColor = modernRed
                    )
                )

                // Choix du niveau d'urgence
                Text("Niveau d'urgence", style = MaterialTheme.typography.bodySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    listOf("Faible", "Moyenne", "Élevée").forEach { level ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = urgencyLevel == level,
                                onClick = { urgencyLevel = level },
                                colors = RadioButtonDefaults.colors(selectedColor = modernRed)
                            )
                            Text(level)
                        }
                    }
                }

                // Bouton d'ajout de photo
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    border = BorderStroke(1.dp, modernRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.ImageIcon, contentDescription = null, tint = modernRed)
                    Spacer(Modifier.width(8.dp))
                    Text("Ajouter une photo", color = modernRed)
                }

                // Aperçu de l'image sélectionnée
                imageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                // Zone pour afficher les messages (snackbar)
                SnackbarHost(hostState = snackbarHostState)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // TODO: Implémenter la logique de soumission
                    scope.launch {
                        isSubmitting = true
                        // submitReport(...)
                        isSubmitting = false
                        snackbarHostState.showSnackbar("Signalement envoyé")
                        onDismiss()
                    }
                },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = modernRed)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(if (isSubmitting) "Envoi..." else "Envoyer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = modernRed)
            }
        },
        shape = RoundedCornerShape(12.dp)
    )
}

// Les fonctions submitReport() et saveReportToFirestore() restent inchangées
