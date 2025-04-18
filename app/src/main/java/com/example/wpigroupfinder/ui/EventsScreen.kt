package com.example.wpigroupfinder.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wpigroupfinder.data.model.Event

val sampleEvents = listOf(
    Event(1, "Robotics Club Meeting", "April 20", "Campus Center", "Join us for robotics fun!","9:00","10:00"),
    Event(2, "Jazz Night", "April 21", "Alden Hall", "Enjoy live jazz music.","7:00","10:00"),
    Event(3, "Hackathon", "April 22", "Innovation Studio", "24-hour coding challenge!","12:00","12:00")
)

@Composable
fun EventListItem(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = event.date, style = MaterialTheme.typography.bodyMedium)
            Text(text = event.location, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreenDesign(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Events List") },
                actions = {
                    IconButton(onClick = { navController.navigate("create_event") }, modifier = Modifier.size(65.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Create Event")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(sampleEvents) { event ->
                    EventListItem(event = event) {
                        navController.navigate("event/${event.id}")
                    }
                }
            }
            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Go to Home")
            }
        }
    }
}
