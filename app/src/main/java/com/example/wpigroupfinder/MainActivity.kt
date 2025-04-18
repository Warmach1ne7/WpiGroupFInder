package com.example.wpigroupfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wpigroupfinder.ui.CreateEventScreenDesign
import com.example.wpigroupfinder.ui.DetailsScreenDesign
import com.example.wpigroupfinder.ui.EventDetailsScreenDesign
import com.example.wpigroupfinder.ui.EventsScreenDesign
import com.example.wpigroupfinder.ui.HomeScreenDesign
import com.example.wpigroupfinder.ui.theme.WPIGroupFinderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WPIGroupFinderTheme {
                MyApp()
            }
        }
    }

    @Composable
    fun MyApp() {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") { HomeScreenDesign(navController) }
            composable("details") { DetailsScreenDesign(navController) }
            composable("events"){ EventsScreenDesign(navController) }
            composable(
                "event/{eventId}",
                arguments = listOf(navArgument("eventId") { type = NavType.IntType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getInt("eventId")
                EventDetailsScreenDesign(navController, eventId)
            }
            composable("create_event") { CreateEventScreenDesign(navController) }
        }
    }
}


