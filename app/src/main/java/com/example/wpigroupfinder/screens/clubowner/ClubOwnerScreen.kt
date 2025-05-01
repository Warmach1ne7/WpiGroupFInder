package com.example.wpigroupfinder.screens.clubowner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubOwnerScreenDesign(navController: NavController, clubid: String, userid: String) { //Basically view club screen
    var userIdInt = userid?.toInt()
    var clubIdInt = clubid?.toInt()
    var clubName by remember { mutableStateOf("") }
    var clubDesc by remember { mutableStateOf("") }
    var memberNum by remember { mutableIntStateOf(0) }
    var clubOwnerName by remember { mutableStateOf("") }
    var clubOwnerUid by remember{ mutableIntStateOf(0) }
    var isMember by remember { mutableStateOf(false) }

    fun setStates(body: JSONObject){
        clubName = body.getString("club_name")
        clubDesc = body.getString("club_desc")
        memberNum = body.getInt("members_num")
        clubOwnerUid = body.getInt("leaderId")
        clubOwnerName = body.getString("leaderName")
        memberNum = body.getInt("members_num")
        isMember = body.getBoolean("isMember")
    }

    LaunchedEffect("test") {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()

            val url = "https://fgehdrx5r6.execute-api.us-east-2.amazonaws.com/wpigroupfinder/viewClub"

            val jsonMediaType = "application/json; charset=utf-8".toMediaType()
            //println(username)
            val jsonBody = """
            {
                "club_uid": $clubIdInt
            }
        """.trimIndent().toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(url)
                .post(jsonBody)
                .build()

            try {
                val response = client.newCall(request).execute()
                response.use {
                    val bodyString = it.body?.string()
                    println("Raw response: $bodyString")

                    if (it.isSuccessful && bodyString != null) {
                        val topLevel = JSONObject(bodyString)
                        val body = topLevel.getJSONObject("body")
                        setStates(body)

                    } else {
                        println("Error: ${it.code}")
                    }
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            }
        }
    }

    fun joinClub(){
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()

            val url = "https://fgehdrx5r6.execute-api.us-east-2.amazonaws.com/wpigroupfinder/joinClub"

            val jsonMediaType = "application/json; charset=utf-8".toMediaType()
            //println(username)
            val jsonBody = """
            {
                "club_uid": $clubIdInt,
                "user_uid": $userIdInt
            }
        """.trimIndent().toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(url)
                .post(jsonBody)
                .build()

            try {
                val response = client.newCall(request).execute()
                response.use {
                    val bodyString = it.body?.string()
                    println("Raw response: $bodyString")

                    if (it.isSuccessful && bodyString != null) {
                        val topLevel = JSONObject(bodyString)
                        val body = topLevel.getJSONObject("body")
                        setStates(body)

                    } else {
                        println("Error: ${it.code}")
                    }
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            }
        }
    }

    fun leaveClub(){
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()

            val url = "https://fgehdrx5r6.execute-api.us-east-2.amazonaws.com/wpigroupfinder/leaveClub"

            val jsonMediaType = "application/json; charset=utf-8".toMediaType()
            //println(username)
            val jsonBody = """
            {
                "club_uid": $clubIdInt,
                "user_uid": $userIdInt
            }
        """.trimIndent().toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(url)
                .post(jsonBody)
                .build()

            try {
                val response = client.newCall(request).execute()
                response.use {
                    val bodyString = it.body?.string()
                    println("Raw response: $bodyString")

                    if (it.isSuccessful && bodyString != null) {
                        val topLevel = JSONObject(bodyString)
                        val body = topLevel.getJSONObject("body")
                        setStates(body)

                    } else {
                        println("Error: ${it.code}")
                    }
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Club Details") }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            if (memberNum != 0) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth()
                ) {
                    // Club Name
                    Text(
                        text = clubName,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Owner Section
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Owner",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = clubOwnerName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    // About Section
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "About",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = clubDesc,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    // Members Section
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Number of Members",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "$memberNum",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    // Action Buttons
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate("user/$userIdInt") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text("Back")
                    }

                    if (clubOwnerUid == userIdInt) {
                        Button(
                            onClick = { navController.navigate("editClub/$clubIdInt/$userIdInt") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text("Edit Club Page")
                        }
                    } else if (!isMember) {
                        Button(
                            onClick = { joinClub() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text("Join Club")
                        }
                    } else if (isMember && clubOwnerUid != userIdInt) {
                        Button(
                            onClick = { leaveClub() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text("Leave Club")
                        }
                    }
                    Button(
                        onClick = { navController.navigate("create_event/$clubName") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text("Add Club Event")
                    }
                    Button(
                        onClick = { navController.navigate("events/${userIdInt}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text("Go To Events")
                    }
                }
            }
        }
    }

}