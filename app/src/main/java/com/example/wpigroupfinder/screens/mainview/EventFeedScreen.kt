package com.example.wpigroupfinder.screens.mainview

import StepCounter
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

@Composable
fun EventFeedScreenDesign(navController: NavController) {
    val context = LocalContext.current
    val stepCounter = remember{ StepCounter(context.applicationContext) }

    LaunchedEffect(Unit) {
        //only called when screen loads
        stepCounter.startListen()
    }

    val stepCount by stepCounter.stepCount.collectAsState()


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Steps today: $stepCount")
            Text("Event Feed")
            Button(onClick = { navController.navigate("login") }) {
                Text("Login")
            }
            Button(onClick = { navController.navigate("map") }) {
                Text("Map")
            }
            val eventID = 101 //id of event you clicked on
            Button(onClick = {
                navController.navigate("viewEvent/$eventID")
            }) {
                Text("View Event $eventID")
            }

            Text("Nav Buttons (To be deleted)")
            //change to if logged in
            Button(onClick = { navController.navigate("user") }) {
                Text("Profile (User)")
            }
            Button(onClick = { navController.navigate("clubOwner") }) {
                Text("Profile (Club Owner)")
            }

        }
    }

}