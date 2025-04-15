package com.example.wpigroupfinder.screens.clubowner

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
fun ClubOwnerScreenDesign(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Club Owner")
            Button(onClick = { navController.navigate("eventFeed") }) {
                Text("Back")
            }
            Button(onClick = { navController.navigate("editClubPage") }) {
                Text("Edit Club Page")
            }
            Button(onClick = { navController.navigate("clubEvents") }) {
                Text("Club Events")
            }
        }
    }

}