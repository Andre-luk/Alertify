package com.example.alertify

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.alertify.Navigation.AlertifyNavHost
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Vérification de l'initialisation de Firebase
        try {
            val app = FirebaseApp.getInstance()
            Log.d("FirebaseCheck", "FirebaseApp name: ${app.name}")
        } catch (e: Exception) {
            Log.e("FirebaseCheck", "FirebaseApp not initialized", e)
        }

        // Envoi d'un événement Analytics de lancement d'application
        try {
            val analytics = Firebase.analytics
            analytics.logEvent("app_launched", null)
            Log.d("FirebaseCheck", "Analytics event 'app_launched' envoyé")
        } catch (e: Exception) {
            Log.e("FirebaseCheck", "Analytics init failed", e)
        }

        // Test d'écriture dans Firestore sans KTX
        try {
            val db = FirebaseFirestore.getInstance()
            db.collection("verification")
                .document("init")
                .set(mapOf("timestamp" to FieldValue.serverTimestamp()))
                .addOnSuccessListener { _: Void? ->
                    Log.d("FirebaseCheck", "✅ Firestore write OK")
                }
                .addOnFailureListener { e: Exception ->
                    Log.e("FirebaseCheck", "❌ Firestore write FAILED", e)
                }
        } catch (e: Exception) {
            Log.e("FirebaseCheck", "Firestore init failed", e)
        }

        // Contenu Compose
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AlertifyNavHost(navController = navController)
                }
            }
        }
    }
}
