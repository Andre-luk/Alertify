package com.example.alertify.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.alertify.screens.auth.*
import com.example.alertify.screens.splash.SplashScreen
import com.example.alertify.screens.DrawingScreen
import com.example.alertify.screens.services.ServicesScreen
import com.example.alertify.screens.incidents.IncidentsScreen
import com.example.alertify.screens.admin.*
import com.example.alertify.screens.alerts.AlertsScreen
import com.example.alertify.screens.users.UsersScreen
import com.example.alertify.screens.report.ReportForm
import com.example.alertify.screens.stats.DashboardStatsScreen
import com.example.alertify.screens.settings.SettingsScreen
import com.example.alertify.screens.account.AccountScreen
import com.example.alertify.screens.main.UserHomeScreen
import com.example.alertify.screens.main.MyAlertsScreen
import com.example.alertify.screens.main.AllAlertsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AlertifyNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {

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

        composable("forgot_password") {
            ForgotPasswordScreen(navController)
        }

        composable("reset_password") {
            ResetPasswordScreen(navController)
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

        composable("alerts") {
            AlertsScreen(navController)
        }

        composable("alertes") {
            AlertsScreen(navController)
        }

        composable("utilisateurs") {
            UsersScreen(navController)
        }

        // ✅ Mise à jour ici avec onDismiss et onSubmit
        composable("report_form") {
            ReportForm(
                onDismiss = { navController.popBackStack() },
                onSubmit = { incident ->
                    // Exemple de logique : simplement revenir en arrière
                    navController.popBackStack()
                }
            )
        }

        // Alias ou doublon si nécessaire
        composable("reportform") {
            ReportForm(
                onDismiss = { navController.popBackStack() },
                onSubmit = { incident ->
                    navController.popBackStack()
                }
            )
        }

        composable("tableau_de_bord") {
            DashboardStatsScreen(navController)
        }

        composable("settings") {
            SettingsScreen()
        }

        composable("account") {
            AccountScreen(onLogout = {
                navController.navigate("login") {
                    popUpTo("account") { inclusive = true }
                }
            })
        }

        composable("user_home") {
            UserHomeScreen(navController)
        }

        composable("my_alerts") {
            MyAlertsScreen(navController)
        }

        composable("all_alerts") {
            AllAlertsScreen(navController)
        }

        composable("admin_access") {
            AdminAccessScreen(navController)
        }

        composable("admin_register") {
            AdminRegisterScreen(navController)
        }

        composable("admin") {
            AdminScreen(navController)
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

        composable("admin_users") {
            AdminUsersScreen(navController)
        }

        composable("admin_manage_admins") {
            AdminAdminsScreen(navController)
        }

        composable("admin_logs") {
            AdminActivityLogScreen(navController)
        }
    }
}
