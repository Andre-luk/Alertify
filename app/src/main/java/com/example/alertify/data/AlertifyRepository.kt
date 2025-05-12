package com.example.alertify.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

// 1. Modèle de données : une alerte simple
data class Alert(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// 2. Interface de votre repository
interface AlertifyRepository {
    suspend fun createAlert(alert: Alert)
    suspend fun getAllAlerts(): List<Alert>
    suspend fun deleteAlert(id: String)
}

// 3. Implémentation Firestore du repository
class AlertifyRepositoryImpl(
    private val firestore: FirebaseFirestore
) : AlertifyRepository {
    private val collection = firestore.collection("alerts")

    override suspend fun createAlert(alert: Alert) {
        // Crée un document avec ID généré automatiquement
        collection.add(alert).await()
    }

    override suspend fun getAllAlerts(): List<Alert> {
        val snapshot: QuerySnapshot = collection
            .orderBy("timestamp")
            .get()
            .await()
        return snapshot.documents
            .mapNotNull { it.toObject(Alert::class.java)?.copy(id = it.id) }
    }

    override suspend fun deleteAlert(id: String) {
        collection.document(id)
            .delete()
            .await()
    }
}
