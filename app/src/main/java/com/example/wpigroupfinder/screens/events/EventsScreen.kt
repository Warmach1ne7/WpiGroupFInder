package com.example.wpigroupfinder.screens.events

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wpigroupfinder.data.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

/*val sampleEvents = listOf(
    Event(1, "Robotics Club Meeting", "April 20", "Campus Center", "Join us for robotics fun!","9:00","10:00"),
    Event(2, "Jazz Night", "April 21", "Alden Hall", "Enjoy live jazz music.","7:00","10:00"),
    Event(3, "Hackathon", "April 22", "Innovation Studio", "24-hour coding challenge!","12:00","12:00")
)*/


suspend fun getAllEvents(): List<Event> = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val url = "https://fgehdrx5r6.execute-api.us-east-2.amazonaws.com/wpigroupfinder/getAllEvents" // Replace with your actual endpoint
    val jsonBody = """
    {
    }
""".trimIndent()
    val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())
    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    val response = client.newCall(request).execute()
    val responseBody = response.body?.string()

    if (!response.isSuccessful || responseBody == null) {
        return@withContext emptyList<Event>()
    }

    val events = mutableListOf<Event>()
    val json = JSONObject(responseBody)
    val eventsArray: JSONArray = json.optJSONArray("events") ?: JSONArray()

    for (i in 0 until eventsArray.length()) {
        val obj = eventsArray.getJSONObject(i)
        events.add(
            Event(
                id = obj.optInt("event_id"),
                title = obj.optString("eventName"),
                date = obj.optString("date"),
                location = obj.optString("location"),
                description = obj.optString("Description"),
                startTime = obj.optString("start_time"),
                endTime = obj.optString("end_time"),
                clubId = obj.optInt("club_id"),
                createdBy = obj.optInt("createdBy")
            )
        )
    }
    events
}

@Composable
fun EventListItem(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = event.date, style = MaterialTheme.typography.bodyMedium)
            //Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Time: ${event.startTime} - ${event.endTime}",
                style = MaterialTheme.typography.bodyMedium
            )
            //Spacer(modifier = Modifier.height(4.dp))
            Text(text = event.location, style = MaterialTheme.typography.bodySmall)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreenDesign(navController: NavController) {
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch events on first composition
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            events = getAllEvents()
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Failed to load events: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

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
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(events) { event ->
                            EventListItem(event = event) {
                                navController.navigate("eventDetails/${event.id}/${7}")//TODO
                            }
                        }
                    }
                }
            }
            Button(
                onClick = { navController.navigate("create_event") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Create Event")
            }
            Button(
                onClick = { navController.navigate("registered_events") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Registered Events")
            }
            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Go to Home")
            }
        }
    }
}
