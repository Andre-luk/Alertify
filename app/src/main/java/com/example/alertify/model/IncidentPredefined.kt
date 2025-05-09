package com.example.alertify.model

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object IncidentPredefined {
    // Liste des types d'incidents
    val INCIDENT_TYPES = listOf(
        "Vol",
        "Agressions",
        "Accident",
        "Infrastructure dangereuse",
        "Incendie",
        "Catastrophe naturelle",
        "Personne disparue",
        "Autre"
    )

    // Mapping par défaut entre type d'incident et service associé
    val DEFAULT_SERVICE_MAPPING = mapOf(
        "Vol" to "Police",
        "Agressions" to "Police",
        "Accident" to "Mairie",
        "Infrastructure dangereuse" to "Gouvernement",
        "Incendie" to "Pompiers",
        "Catastrophe naturelle" to "Gouvernement",
        "Personne disparue" to "Police"
    )

    /**
     * Formulaire de signalement d'incident.
     *
     * @param alertType Le type d'incident sélectionné (issu de INCIDENT_TYPES)
     * @param isUserLoggedIn Indique si l'utilisateur est connecté ou non
     * @param onSubmit Callback déclenché lors du clic sur "Soumettre".
     *                 Pour le type "Autre" :
     *                 - Utilisateur non connecté : (date, lieu, preciseLieu, email, mot de passe, nom de l'alerte)
     *                 - Utilisateur connecté : (date, lieu, preciseLieu, null, null, nom de l'alerte)
     *                 Pour les autres types, le paramètre incidentName sera null.
     * @param onDismiss Callback déclenché lors de l'annulation ou de la fermeture du formulaire
     */
    @Composable
    fun ReportIncidentForm(
        alertType: String,
        isUserLoggedIn: Boolean,
        onSubmit: (
            date: String,
            lieu: String,
            preciseLieu: String, // Pour "Autre", ce champ restera vide.
            email: String?,
            password: String?,
            incidentName: String?
        ) -> Unit,
        onDismiss: () -> Unit
    ) {
        // Si l'incident est de type "Autre", le formulaire est personnalisé
        if (alertType == "Autre") {
            // Date par défaut configurée avec SimpleDateFormat (compatible API 24)
            val defaultDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            var date by remember { mutableStateOf(defaultDate) }
            var lieu by remember { mutableStateOf("") }
            var incidentName by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { onDismiss() },
                title = { Text("Signaler un Incident") },
                text = {
                    Column {
                        // Nom de l'alerte
                        OutlinedTextField(
                            value = incidentName,
                            onValueChange = { incidentName = it },
                            label = { Text("Nom de l'alerte") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Date
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Date") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Lieu
                        OutlinedTextField(
                            value = lieu,
                            onValueChange = { lieu = it },
                            label = { Text("Lieu") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Si l'utilisateur n'est pas connecté, affiche email et mot de passe
                        if (!isUserLoggedIn) {
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Mot de passe") },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        // Pour "Autre", on passe un champ preciseLieu vide
                        if (!isUserLoggedIn) {
                            // Utilisateur non connecté : envoie nom de l'alerte, date, email, mot de passe, lieu
                            onSubmit(date, lieu, "", email, password, incidentName)
                        } else {
                            // Utilisateur connecté : envoie nom de l'alerte, date, lieu uniquement
                            onSubmit(date, lieu, "", null, null, incidentName)
                        }
                        onDismiss()
                    }) {
                        Text("Soumettre")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onDismiss() }) {
                        Text("Annuler")
                    }
                }
            )
        } else {
            // Pour les autres types d'incidents, on conserve le formulaire par défaut
            val defaultDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            var date by remember { mutableStateOf(defaultDate) }
            var lieu by remember { mutableStateOf("") }
            var preciseLieu by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { onDismiss() },
                title = { Text("Signaler un Incident") },
                text = {
                    Column {
                        // Date
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Date") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Lieu
                        OutlinedTextField(
                            value = lieu,
                            onValueChange = { lieu = it },
                            label = { Text("Lieu") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Précisez le lieu
                        OutlinedTextField(
                            value = preciseLieu,
                            onValueChange = { preciseLieu = it },
                            label = { Text("Précisez le lieu") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Affiche les champs email et mot de passe uniquement si l'utilisateur n'est pas connecté
                        if (!isUserLoggedIn) {
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Mot de passe") },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        onSubmit(
                            date,
                            lieu,
                            preciseLieu,
                            if (!isUserLoggedIn) email else null,
                            if (!isUserLoggedIn) password else null,
                            null
                        )
                        onDismiss()
                    }) {
                        Text("Soumettre")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onDismiss() }) {
                        Text("Annuler")
                    }
                }
            )
        }
    }
}
