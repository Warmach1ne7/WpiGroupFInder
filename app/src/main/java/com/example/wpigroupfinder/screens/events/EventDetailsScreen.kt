package com.example.wpigroupfinder.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import com.example.wpigroupfinder.data.model.Event
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreenDesign(navController: NavController, eventId: Int?) {
    var event by remember { mutableStateOf<Event?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch event details when screen loads
    LaunchedEffect(eventId) {
        if (eventId != null) {
            try {
                event = getEventRequest(eventId)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Failed to load event: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
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
            event != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp)
                ) {
                    Text(
                        text = event!!.title,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Date: ${event!!.date}     Time: ${event!!.startTime} - ${event!!.endTime}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Location: ${event!!.location}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = event!!.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("Event not found", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

suspend fun getEventRequest(eventId: Int): Event? = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val url = "https://fgehdrx5r6.execute-api.us-east-2.amazonaws.com/wpigroupfinder/getEvent"
    val jsonBody = """
    {
        "eventId": $eventId
    }
    """.trimIndent()
    val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())
    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    try {
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        if (response.isSuccessful && responseBody != null) {
            val json = JSONObject(responseBody)
            val eventJson = json.getJSONObject("event") // <-- FIXED
            Event(
                id = eventJson.optInt("id"),
                title = eventJson.optString("title"),
                date = eventJson.optString("date"),
                location = eventJson.optString("location"),
                description = eventJson.optString("description"),
                startTime = eventJson.optString("startTime"),
                endTime = eventJson.optString("endTime"),
                clubId = eventJson.optInt("clubId"),
                createdBy = eventJson.optInt("createdBy")
            )
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

