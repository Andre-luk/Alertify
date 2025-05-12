package com.example.alertify.screens.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.alertify.R
import com.example.alertify.screens.report.ReportForm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val storage = FirebaseStorage.getInstance()
    val coroutineScope = rememberCoroutineScope()
    val modernRed = Color(0xFFD32F2F)

    var photoUri by remember { mutableStateOf(user?.photoUrl) }
    var displayName by remember { mutableStateOf(user?.displayName ?: user?.email.orEmpty()) }

    LaunchedEffect(user) {
        user?.reload()?.addOnCompleteListener {
            photoUri = user.photoUrl
            displayName = user.displayName ?: user.email.orEmpty()
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            user?.let { currentUser ->
                val ref = storage.reference.child("profile_pics/${currentUser.uid}.jpg")
                ref.putFile(selectedUri)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                            val update = UserProfileChangeRequest.Builder()
                                .setPhotoUri(downloadUrl)
                                .build()
                            currentUser.updateProfile(update)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        coroutineScope.launch {
                                            currentUser.reload().addOnCompleteListener {
                                                photoUri = currentUser.photoUrl
                                            }
                                        }
                                    }
                                }
                        }
                    }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = modernRed,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_alertify),
                            contentDescription = "Logo Alertify",
                            modifier = Modifier
                                .size(48.dp)
                                .padding(end = 12.dp)
                        )
                        Text(
                            text = "Alertify",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = photoUri),
                    contentDescription = "Photo de profil",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .clickable { imagePicker.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            val actions = listOf(
                ActionItem("Signaler incident", Icons.Default.ReportProblem) {
                    navController.navigate("report_form")
                },
                ActionItem("Mes alertes", Icons.Default.Assignment) {
                    navController.navigate("my_alerts")
                },
                ActionItem("Toutes les alertes", Icons.Default.List) {
                    navController.navigate("all_alerts")
                },
                ActionItem("Déconnexion", Icons.Default.ExitToApp) {
                    auth.signOut()
                    navController.navigate("login") { popUpTo("user_home") { inclusive = true } }
                }
            )

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                actions.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEach { item ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(120.dp)
                                    .clickable { item.onClick() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(32.dp),
                                        tint = modernRed
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = item.label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class ActionItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)