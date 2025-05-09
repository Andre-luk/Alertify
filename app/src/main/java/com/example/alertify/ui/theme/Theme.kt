package com.example.alertify.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Schéma pour le mode sombre en remplaçant primary par FacebookBlue
private val DarkColorScheme = darkColorScheme(
    primary = FacebookBlue,
    secondary = PurpleGrey80,  // Tu peux aussi modifier cette couleur si besoin
    tertiary = Pink80
)

// Schéma pour le mode clair en remplaçant primary par FacebookBlue
private val LightColorScheme = lightColorScheme(
    primary = FacebookBlue,
    secondary = PurpleGrey40,  // Même remarque que pour le mode sombre
    tertiary = Pink40
    // Autres couleurs par défaut peuvent être surchargées ici si nécessaire
)

@Composable
fun AlertifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Ta typographie personnalisée ou celle par défaut
        content = content
    )
}
