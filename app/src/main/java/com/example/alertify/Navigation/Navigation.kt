package com.example.alertify.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.alertify.screens.auth.*
import com.example.alertify.screens.splash.SplashScreen
import com.example.alertify.screens.DrawingScreen
import com.example.alertify.screens.services.ServicesScreen
import com.example.alertify.screens.incidents.IncidentsScreen
import com.example.alertify.screens.admin.AdminAccessScreen
import com.example.alertify.screens.admin.AdminRegisterScreen
import com.example.alertify.screens.admin.AdminScreen
import com.example.alertify.screens.admin.AdminDashboardScreen
import com.example.alertify.screens.admin.AdminIncidentsScreen
import com.example.alertify.screens.admin.AdminServicesScreen
import com.example.alertify.screens.admin.AdminUsersScreen
import com.example.alertify.screens.admin.AdminAdminsScreen
import com.example.alertify.screens.admin.AdminActivityLogScreen
import com.example.alertify.screens.alerts.AlertsScreen
import com.example.alertify.screens.users.UsersScreen
import com.example.alertify.screens.report.ReportForm
import com.example.alertify.screens.stats.DashboardStatsScreen
import com.example.alertify.screens.settings.SettingsScreen
import com.example.alertify.screens.account.AccountScreen
import com.example.alertify.screens.main.UserHomeScreen
import com.example.alertify.screens.main.MyAlertsScreen
import com.example.alertify.screens.main.AllAlertsScreen

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

        // Drawing Screen
        composable("drawing") {
            DrawingScreen()
        }

        // Services Screen
        composable("services") {
            ServicesScreen(navController)
        }

        // Incidents list
        composable("incidents/{defaultIncidentType}") { backStackEntry ->
            val defaultIncidentType = backStackEntry.arguments?.getString("defaultIncidentType") ?: ""
            IncidentsScreen(navController, defaultIncidentType)
        }

        // Legacy alerts route
        composable("alerts") {
            AlertsScreen(navController)
        }

        // Admin dashboard card routes
        composable("alertes") {
            AlertsScreen(navController)
        }

        composable("utilisateurs") {
            UsersScreen(navController)
        }

        composable("report_form") {
            ReportForm(onDismiss = { navController.popBackStack() })
        }

        composable("reportform") {
            ReportForm(onDismiss = { navController.popBackStack() })
        }

        composable("tableau_de_bord") {
            DashboardStatsScreen(navController)
        }

        // Settings & Account
        composable("settings") {
            SettingsScreen()  // Aucun argument nécessaire
        }

        composable("account") {
            AccountScreen(onLogout = {
                navController.navigate("login") {
                    popUpTo("account") { inclusive = true }
                }
            })
        }

        // User home and related
        composable("user_home") {
            UserHomeScreen(navController)
        }
        composable("my_alerts") {
            MyAlertsScreen(navController)
        }
        composable("all_alerts") {
            AllAlertsScreen(navController)
        }

        // Admin screens
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
