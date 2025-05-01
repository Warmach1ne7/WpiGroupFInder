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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import com.example.wpigroupfinder.data.model.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreenDesign(navController: NavController, eventId: Int?) {
    var event by remember { mutableStateOf<Event?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showRegisterDialog by remember { mutableStateOf(false) }
    var registrationResult by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
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
                    Button(
                        onClick = { showRegisterDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                    ) {
                        Text("Register for Event")
                    }


                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Event not found", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        if (showRegisterDialog) {
            AlertDialog(
                onDismissRequest = { showRegisterDialog = false },
                title = { Text("Register for Event") },
                text = { Text("Do you want to register for this event?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showRegisterDialog = false
                            if (event != null) {
                                if (eventId != null) {
                                    registerForEvent(eventId, userId = 7, snackbarHostState, navController/* TODO your user id */) { result ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(result)
                                            if (result == "Registration successful!") {
                                                navController.popBackStack()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    ) { Text("Yes") }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showRegisterDialog = false }
                    ) { Text("No") }
                }
            )
        }
        registrationResult?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp)
            )
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

fun registerForEvent(
    eventId: Int,
    userId: Int,
    snackbarHostState: SnackbarHostState,
    navController: NavController,
    onResult: (String) -> Unit
) {
    val client = OkHttpClient()
    val url = "https://fgehdrx5r6.execute-api.us-east-2.amazonaws.com/wpigroupfinder/registerEvent"
    val jsonBody = """
        {
            "event_id": $eventId,
            "userId": $userId
        }
    """.trimIndent()
    val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())

    // Use the Composable's coroutine scope
    CoroutineScope(Dispatchers.Main).launch {
        try {
            val response = withContext(Dispatchers.IO) {
                client.newCall(
                    Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build()
                ).execute()
            }

            val responseBody = response.body?.string()
            if (response.isSuccessful) {
                // Show snackbar and navigate back
                snackbarHostState.showSnackbar(
                    "Registration successful!",
                    duration = SnackbarDuration.Short
                )
                // Wait for snackbar to complete before navigating
                delay(1500)
                navController.popBackStack()
            } else {
                onResult("Registration failed: ${response.code} - $responseBody")
            }
        } catch (e: Exception) {
            onResult("Error: ${e.localizedMessage}")
        }
    }
}



