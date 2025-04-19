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
import com.example.wpigroupfinder.screens.clubowner.ClubEventsScreenDesign
import com.example.wpigroupfinder.screens.clubowner.ClubOwnerScreenDesign
import com.example.wpigroupfinder.screens.clubowner.EditClubPageScreenDesign
import com.example.wpigroupfinder.screens.clubowner.EditEventScreenDesign
import com.example.wpigroupfinder.screens.mainview.EventFeedScreenDesign
import com.example.wpigroupfinder.screens.login.LoginScreenDesign
import com.example.wpigroupfinder.screens.mainview.MapScreenDesign
import com.example.wpigroupfinder.screens.mainview.VerificationScreenDesign
import com.example.wpigroupfinder.screens.mainview.ViewClubPageScreenDesign
import com.example.wpigroupfinder.screens.mainview.ViewEventScreenDesign
import com.example.wpigroupfinder.ui.theme.WPIGroupFinderTheme

import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat



class MainActivity : ComponentActivity() {
    private val ALL_PERMISSIONS = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACTIVITY_RECOGNITION
    )

    //https://developer.android.com/training/permissions/requesting
    private val allPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val activity = permissions[android.Manifest.permission.ACTIVITY_RECOGNITION]?: false
            val fine = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION]?: false
            val coarse = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION]?: false
            if (fine && coarse && activity) {
                Log.d("Permission", "All permissions granted")
            } else {
                Log.d("Permission", "Permissions denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("Permission", "Launching permissions request. . .")
            allPermissionsLauncher.launch(ALL_PERMISSIONS)
        }

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
            startDestination = "eventFeed"
        ) {
            //login
            composable("login") { LoginScreenDesign(navController) }

            //clubOwner
            composable("clubOwner") { ClubOwnerScreenDesign(navController) }
            composable("clubEvents") { ClubEventsScreenDesign(navController) }
            composable("editClubPage") { EditClubPageScreenDesign(navController) }
            composable("editEvent") { EditEventScreenDesign(navController) }

            //all visible
            composable("eventFeed") { EventFeedScreenDesign(navController) }
            composable("map") { MapScreenDesign(navController) }
            composable(
                route = "viewEvent/{eventId}",
                arguments = listOf(navArgument("eventId") { type = NavType.IntType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getInt("eventId")
                ViewEventScreenDesign(navController, eventId)
            }
            composable("viewClubPage") { ViewClubPageScreenDesign(navController) } //will need extra args
            composable("verification") { VerificationScreenDesign(navController) } //will need extra args
        }
    }
}

