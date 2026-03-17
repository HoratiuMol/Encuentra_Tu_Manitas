package com.example.encuentratumanitas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.encuentratumanitas.ui.theme.EncuentraTuManitasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EncuentraTuManitasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "index"
    ) {
        composable("index") {
            IndexScreen(
                onNavigateToAuth      = { navController.navigate("auth") },
                onNavigateToDashboard = { navController.navigate("dashboard") },
                onNavigateToAdmin     = { navController.navigate("admin") }
            )
        }
        composable("auth") {
            AuthScreen(
                onNavigateToDashboard = { navController.navigate("dashboard") },
                onNavigateToManitas   = { navController.navigate("manitas") },
                onNavigateToAdmin     = { navController.navigate("admin") }
            )
        }
        composable("dashboard") {
            ClientDashboardScreen(
                onNavigateToAuth = {
                    navController.navigate("auth") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }
        composable("admin") {
            // AdminScreen(navController)
        }
        composable("manitas") {
            // ManitasDashboardScreen(navController)
        }
    }
}
