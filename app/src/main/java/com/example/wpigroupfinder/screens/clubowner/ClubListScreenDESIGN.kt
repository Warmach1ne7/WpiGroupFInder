package com.example.wpigroupfinder.screens.clubowner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubListScreenDesign(navController: NavController, user_uid: String?) {
    var clubs by remember { mutableStateOf<List<Map<String, Any?>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            clubs = getAllClubs()
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Failed to load clubs: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clubs List") },
                actions = {
                    IconButton(onClick = { navController.navigate("user/${user_uid}") }, modifier = Modifier.size(65.dp)) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Account")
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
                        items(clubs) { club ->
                            ClubListItem(club = club) {
                                navController.navigate("clubOwner/${club["id"]}/${user_uid}")
                            }
                        }
                    }
                }
            }
            Button(
                onClick = { navController.navigate("events/${user_uid}") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Events")
            }
            Button(
                onClick = { navController.navigate("registered_events/${user_uid}") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Registered Events")
            }
            Button(
                onClick = { navController.navigate("map/${user_uid}") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Map")
            }
        }
    }
}

@Composable
fun ClubListItem(club: Map<String, Any?>, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = club["name"] as? String ?: "Unknown",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = club["description"] as? String ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
            //Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Location: ${club["location"] as? String ?: ""}",
                style = MaterialTheme.typography.bodyMedium
            )
            //Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "President: ${club["president"] as? String ?: ""}",
                style = MaterialTheme.typography.bodySmall
            )
        }

    }
}
suspend fun getAllClubs(): List<Map<String, Any?>> = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val url = "https://fgehdrx5r6.execute-api.us-east-2.amazonaws.com/wpigroupfinder/getAllClubs" // Replace with your endpoint
    val jsonBody = "{}"
    val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())
    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    val response = client.newCall(request).execute()
    val responseBody = response.body?.string()

    if (!response.isSuccessful || responseBody == null) {
        return@withContext emptyList<Map<String, Any?>>()
    }

    val clubs = mutableListOf<Map<String, Any?>>()
    val json = JSONObject(responseBody)
    val clubsArray: JSONArray = json.optJSONArray("clubs") ?: JSONArray()

    for (i in 0 until clubsArray.length()) {
        val obj = clubsArray.getJSONObject(i)
        clubs.add(
            mapOf(
                "id" to obj.optInt("id"),
                "name" to obj.optString("name"),
                "description" to obj.optString("description"),
               // "location" to obj.optString("location"),
                "president" to obj.optString("leader")
            )
        )
    }
    clubs
}
