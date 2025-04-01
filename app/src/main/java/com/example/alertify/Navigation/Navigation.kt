package com.example.alertify.navigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.alertify.screens.auth.LoginScreen
import com.example.alertify.screens.auth.RegisterScreen
import com.example.alertify.screens.auth.WelcomeScreen
import com.example.alertify.screens.main.HomeScreen
import com.example.alertify.screens.main.UserHomeScreen // Assurez-vous d'avoir un UserHomeScreen défini
import com.example.alertify.screens.splash.SplashScreen
import androidx.compose.runtime.Composable
import com.example.alertify.screens.DrawingScreen



@Composable
fun AlertifyNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("welcome") {
            WelcomeScreen(navController)
        }
        composable("home") {
            HomeScreen(navController)
        }
        composable("drawing") {
            DrawingScreen()
        }
        composable("login") {
            LoginScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("user_home") {
            val isUserLoggedIn = true
            val userName = "Utilisateur"

            UserHomeScreen(navController, isUserLoggedIn, userName)
        }

    }
}
