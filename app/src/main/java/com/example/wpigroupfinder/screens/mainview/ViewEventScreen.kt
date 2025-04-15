package com.example.wpigroupfinder.screens.mainview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun ViewEventScreenDesign(navController: NavController, eventID: Int?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("View Event $eventID")
            Button(onClick = { navController.navigate("eventFeed") }) {
                Text("Back To Event Feed")
            }
            Button(onClick = { navController.navigate("verification") }) {
                Text("Register for Event")
            }
            Button(onClick = { navController.navigate("viewClubPage") }) {
                Text("View Club Page")
            }
        }
    }

}