// app/src/main/java/com/example/alertify/ui/AlertsViewModel.kt
package com.example.alertify.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertify.data.Alert
import com.example.alertify.data.AlertifyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val repo: AlertifyRepository
) : ViewModel() {

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status

    fun addAlert(title: String, message: String) {
        val alert = Alert(title = title, message = message)
        viewModelScope.launch {
            try {
                repo.createAlert(alert)
                _status.value = "✅ Alerte enregistrée"
            } catch (e: Exception) {
                _status.value = "❌ Erreur : ${e.localizedMessage}"
            }
        }
    }
}
