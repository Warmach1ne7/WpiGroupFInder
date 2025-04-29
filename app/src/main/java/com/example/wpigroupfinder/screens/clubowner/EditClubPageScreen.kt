package com.example.wpigroupfinder.screens.clubowner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@Composable

fun EditClubPageScreenDesign(navController: NavController, clubid: String, userid: String) {
    var userIdInt = userid?.toInt()
    var clubIdInt = clubid?.toInt()
    var clubName by remember { mutableStateOf("") }
    var clubDesc by remember { mutableStateOf("") }
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
                        clubName = body.getString("club_name")
                        clubDesc = body.getString("club_desc")

                    } else {
                        println("Error: ${it.code}")
                    }
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            }
        }
    }

    fun editClubRequest(){
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()

            val url = "https://fgehdrx5r6.execute-api.us-east-2.amazonaws.com/wpigroupfinder/editClub"

            val jsonMediaType = "application/json; charset=utf-8".toMediaType()

            val jsonBody = """
            {
                "club_uid": "$clubIdInt",
                "club_name": "$clubName",
                "club_desc": "$clubDesc"
            }
        """.trimIndent().toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(url)
                .post(jsonBody)
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    println("Success: ${JSONObject(response.body?.string())}")
                } else {
                    println("Error: ${response.code}")
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            }
        }
        navController.navigate("clubOwner/${clubIdInt}/${userIdInt}")
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Edit Club Page")
            OutlinedTextField(
                value = clubName,
                onValueChange = { clubName = it },
                label = { Text("Club Name") }
            )
            OutlinedTextField(
                value = clubDesc,
                onValueChange = { clubDesc = it },
                label = { Text("About Club") }
            )
            Button(onClick = {editClubRequest()}) {
                Text("Save Changes")
            }
        }
    }

}