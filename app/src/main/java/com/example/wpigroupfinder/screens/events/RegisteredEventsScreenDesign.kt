package com.example.wpigroupfinder.screens.events

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisteredEventsScreenDesign(navController: NavController, userId: String?) {
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch registered events on first composition
    LaunchedEffect(userId) {
        try {
            isLoading = true
            events = getRegisteredEvents(userId)
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Failed to load registered events: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Registered Events") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                }
            }
            events.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    items(events) { event ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { navController.navigate("eventDetails/${event.id}") }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(event.title, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Date: ${event.date}", style = MaterialTheme.typography.bodyMedium)
                                Text("Time: ${event.startTime} - ${event.endTime}", style = MaterialTheme.typography.bodyMedium)
                                Text("Location: ${event.location}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("You have not registered for any events.", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

// Helper function to fetch registered events for a user
suspend fun getRegisteredEvents(userId: String?): List<Event> = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val url = "https://fgehdrx5r6.execute-api.us-east-2.amazonaws.com/wpigroupfinder/getRegisteredEvents"
    val jsonBody = """
        {
            "userId": $userId  
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
                id = obj.optInt("id"),
                title = obj.optString("title"),
                date = obj.optString("date"),
                location = obj.optString("location"),
                description = obj.optString("description"),
                startTime = obj.optString("startTime"),
                endTime = obj.optString("endTime"),
                clubId = obj.optInt("clubId"),
                createdBy = obj.optInt("createdBy")
            )
        )
    }
    events
}
