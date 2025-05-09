package com.example.alertify.screens.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val primaryRed = Color(0xFFF44336)

    var notificationEnabled by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var themeDark by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("Français") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val languageOptions = listOf("Français", "English", "Español")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paramètres") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryRed, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Préférences", style = MaterialTheme.typography.headlineMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Notifications", modifier = Modifier.weight(1f))
                Switch(checked = notificationEnabled, onCheckedChange = { notificationEnabled = it })
            }

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Modifier votre email") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.LightMode, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thème sombre", modifier = Modifier.weight(1f))
                Switch(checked = themeDark, onCheckedChange = { themeDark = it })
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Language, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Langue", modifier = Modifier.weight(1f))
                DropdownMenuLanguage(selectedLanguage, languageOptions) { selectedLanguage = it }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        Toast
                            .makeText(context, "Redirection vers politique de confidentialité", Toast.LENGTH_SHORT)
                            .show()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.PrivacyTip, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Politique de confidentialité & Conditions")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    Toast.makeText(context, "Paramètres sauvegardés", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryRed, contentColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sauvegarder")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDeleteDialog = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Supprimer mon compte", color = Color.Red)
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showDeleteDialog = false
                            Toast.makeText(context, "Compte supprimé", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("Oui", color = primaryRed)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Annuler")
                        }
                    },
                    title = { Text("Confirmation") },
                    text = { Text("Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.") }
                )
            }
        }
    }
}

@Composable
fun DropdownMenuLanguage(
    selectedLanguage: String,
    languageOptions: List<String>,
    onLanguageSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Text(
            text = selectedLanguage,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(8.dp),
            color = MaterialTheme.colorScheme.primary
        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            languageOptions.forEach { language ->
                DropdownMenuItem(
                    text = { Text(language) },
                    onClick = {
                        onLanguageSelected(language)
                        expanded = false
                    }
                )
            }
        }
    }
}
