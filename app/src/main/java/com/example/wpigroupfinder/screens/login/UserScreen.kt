package com.example.wpigroupfinder.screens.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun UserScreenDesign(navController: NavController, user_uid: String?) {
    val user_uidInt = user_uid?.toInt()
    var username by remember{ mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var profilePic by remember { mutableStateOf("https://wpigroupfinder.s3.us-east-2.amazonaws.com/images/test_pfp.jpg") }
    val steps by GlobalStepCounter.stepCounter.stepCount.collectAsState()
    val clubsList = remember { mutableStateOf<JSONArray?>(null) }
    LaunchedEffect("test") {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()

            val url = "https://fgehdrx5r6.execute-api.us-east-2.amazonaws.com/wpigroupfinder/getUser"

            val jsonMediaType = "application/json; charset=utf-8".toMediaType()
            //println(username)
            println("uid: $user_uidInt")
            val jsonBody = """
            {
                "user_uid": $user_uidInt
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
                        val user = body.getJSONObject("user")
                        username = user.getString("username")
                        description = user.getString("description")
                        val joinedClubs = user.optJSONArray("joinedClubs") // returns null if not found
                        clubsList.value = joinedClubs
                        println("User UID: $user_uid")
                    } else {
                        println("Error: ${it.code}")
                    }
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = profilePic,
                contentDescription = "test image"
            )
            Text("WPI Group Finder User Page")
            Text("$user_uidInt")
            Text("$username")
            //Text("$description")
            Text("Steps taken today: $steps")
            Column {
                Text("Clubs")
//                clubsList.value.forEach{club ->
//                    Text(text = club.getString("name"),
//                        modifier = Modifier.clickable { println(club.getString("club_uid")) })
//                }
//                if (clubsList.value?.length() == 0) {
//                    Text("No clubs currently")
//                }
                clubsList.value?.let { jsonArray ->
                    (0 until jsonArray.length()).forEach { i ->
                        val club = jsonArray.getJSONObject(i)
                        // Use club here
                        Text(text = club.getString("name"),
                            modifier = Modifier.clickable {
                                val clubID = club.getString("club_uid")
                                navController.navigate("clubOwner/${clubID}/${user_uidInt}")
                            })
                    }
                }

                Button(onClick = {navController.navigate("createClub/${user_uidInt}")}){
                        Text("Create Club")
                    }
                Spacer(modifier = Modifier
                    .height(50.dp)
                )


                Button(onClick = { navController.navigate("events/${user_uidInt}") }){
                    Text("To Events")
                }



                Button(onClick = { navController.navigate("login") }){
                    Text("Sign Out")
                }
            }



        }
    }

}
