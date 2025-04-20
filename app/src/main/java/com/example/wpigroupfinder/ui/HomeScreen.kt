
package com.example.wpigroupfinder.ui

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

fun EditClubPageScreenDesign(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Edit Club Page")
            Button(onClick = { navController.navigate("clubOwner") }) {
                Text("Back to Club Owner")
            }


            Button(onClick = { navController.navigate("events") }) {
                Text("Go to events")
            }
        }
    }

}