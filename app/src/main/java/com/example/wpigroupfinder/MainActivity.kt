package com.example.wpigroupfinder

import GlobalStepCounter
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
import com.example.wpigroupfinder.screens.events.CreateEventScreenDesign
import com.example.wpigroupfinder.screens.events.EventDetailsScreenDesign
import com.example.wpigroupfinder.screens.events.EventsScreenDesign
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
import com.example.wpigroupfinder.screens.login.FaceRecogScreenDesign
import com.example.wpigroupfinder.screens.login.SignupScreenDesign
import com.example.wpigroupfinder.screens.clubowner.CreateClubScreenDesign
import com.example.wpigroupfinder.screens.login.UserScreenDesign


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

        GlobalStepCounter.init(this)

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
            startDestination = "login"
        ) {
            //login
            composable("login") { LoginScreenDesign(navController) }
            composable("signup") { SignupScreenDesign(navController) }

            //clubOwner
            composable("clubOwner/{clubid}/{userid}") {
                backStackEntry ->
                val clubid = backStackEntry.arguments?.getString("clubid")
                val userid = backStackEntry.arguments?.getString("userid")
                ClubOwnerScreenDesign(navController, clubid!!, userid!!) }
            composable("clubEvents") { ClubEventsScreenDesign(navController) }
            composable("editClub/{clubid}/{userid}") {
                backStackEntry ->
                val clubid = backStackEntry.arguments?.getString("clubid")
                val userid = backStackEntry.arguments?.getString("userid")
                EditClubPageScreenDesign(navController, clubid!!, userid!!)
            }
            composable("editEvent") { EditEventScreenDesign(navController) }
            composable("createClub/{userid}") { backStackEntry ->
                val userid = backStackEntry.arguments?.getString("userid")
                CreateClubScreenDesign(navController, userid) }

            composable("signup") { SignupScreenDesign(navController) }

            composable("user/{userid}") { backStackEntry ->
                val userid = backStackEntry.arguments?.getString("userid")
                UserScreenDesign(navController, userid) }

            //all visible
            composable("map/{userid}") {
                    backStackEntry ->
                val userid = backStackEntry.arguments?.getString("userid")
                MapScreenDesign(navController, userid) }
            composable(
                route = "viewEvent/{eventId}",
                arguments = listOf(navArgument("eventId") { type = NavType.IntType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getInt("eventId")
                ViewEventScreenDesign(navController, eventId)
            }
            composable("viewClubPage") { ViewClubPageScreenDesign(navController) } //will need extra args
            composable("verification") { VerificationScreenDesign(navController) } //will need extra args

            composable("events/{userid}"){
                backStackEntry ->
                val userid = backStackEntry.arguments?.getString("userid")
                EventsScreenDesign(navController, userid)
            }
            composable(
                "eventDetails/{eventId}",
                arguments = listOf(navArgument("eventId") { type = NavType.IntType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getInt("eventId")
                EventDetailsScreenDesign(navController, eventId)
            }
            composable("create_event") { CreateEventScreenDesign(navController) }
            composable("faceRecog") { FaceRecogScreenDesign(navController) }
        }
    }
}

