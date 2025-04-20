package com.example.wpigroupfinder.screens.events

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController


@Composable
fun DetailsScreenDesign(navController: NavController) {
    Text("Details Screen")
    Button(onClick = { navController.navigate("home") }) {
        Text("Go to Home")
    }
}

