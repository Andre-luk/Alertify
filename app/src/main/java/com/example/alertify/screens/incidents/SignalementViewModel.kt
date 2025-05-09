package com.example.alertify.screens.incidents

import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import java.util.*

class SignalementViewModel : ViewModel() {
    var markerPosition = mutableStateOf(LatLng(48.8566, 2.3522)) // Paris par défaut
        private set

    var address = mutableStateOf("")

    fun updatePosition(newPosition: LatLng) {
        markerPosition.value = newPosition
    }

    fun confirmSelection(context: Context) {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(
            markerPosition.value.latitude,
            markerPosition.value.longitude,
            1
        )
        address.value = addresses?.firstOrNull()?.getAddressLine(0) ?: "Adresse inconnue"
    }
}
