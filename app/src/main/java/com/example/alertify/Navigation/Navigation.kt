package com.example.alertify.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.alertify.screens.auth.LoginScreen
import com.example.alertify.screens.auth.RegisterScreen
import com.example.alertify.screens.auth.WelcomeScreen
import com.example.alertify.screens.main.HomeScreen
import com.example.alertify.screens.main.UserHomeScreen
import com.example.alertify.screens.splash.SplashScreen
import com.example.alertify.screens.DrawingScreen
import com.example.alertify.screens.services.ServicesScreen
import com.example.alertify.screens.incidents.IncidentsScreen
import com.example.alertify.screens.admin.AdminScreen
import com.example.alertify.screens.alerts.AlertsScreen
import com.example.alertify.screens.settings.SettingsScreen
import com.example.alertify.screens.account.AccountScreen
import com.example.alertify.screens.admin.AdminActivityLogScreen
import com.example.alertify.screens.admin.AdminAdminsScreen
import com.example.alertify.screens.auth.ForgotPasswordScreen
import com.example.alertify.screens.auth.ResetPasswordScreen
import com.example.alertify.screens.admin.AdminDashboardScreen
import com.example.alertify.screens.admin.AdminIncidentsScreen
import com.example.alertify.screens.admin.AdminServicesScreen
import com.example.alertify.screens.admin.AdminUsersScreen

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

        composable("login") {
            LoginScreen(navController)
        }

        composable("register") {
            RegisterScreen(navController)
        }

        composable("home") {
            HomeScreen(navController)
        }

        composable("drawing") {
            DrawingScreen()
        }

        composable("services") {
            ServicesScreen(navController)
        }

        composable("incidents/{defaultIncidentType}") { backStackEntry ->
            val defaultIncidentType = backStackEntry.arguments?.getString("defaultIncidentType") ?: ""
            IncidentsScreen(navController, defaultIncidentType)
        }

        composable("admin") {
            AdminScreen(navController)
        }

        composable("user_home") {
            // À adapter plus tard avec ton vrai système d’auth
            val isUserLoggedIn = true
            val userName = "Utilisateur"
            UserHomeScreen(navController, isUserLoggedIn, userName)
        }

        composable("alerts") {
            AlertsScreen()
        }

        composable("settings") {
            SettingsScreen()
        }

        composable("account") {
            AccountScreen()
        }

        composable("forgot_password") {
            ForgotPasswordScreen(navController)
        }

        composable("reset_password") {
            ResetPasswordScreen(navController)
        }

        composable("admin_dashboard") {
            AdminDashboardScreen(navController)
        }

        composable("admin_incidents") {
            AdminIncidentsScreen(navController)
        }

        composable("admin_services") {
            AdminServicesScreen(navController)
        }

        // Corrigé : route pour gérer les admins
        composable("admin_manage_admins") {
            AdminAdminsScreen(navController)
        }

        composable("admin_logs") {
            AdminActivityLogScreen(navController)
        }

        composable("admin_users") {
            AdminUsersScreen(navController)
        }

        // Si besoin, dé-commente et ajuste ultérieurement
        // composable("signalement_map") {
        //     SignalementScreen(navController = navController)
        // }
    }
}
