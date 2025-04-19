package com.example.wpigroupfinder.screens.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import coil.compose.AsyncImage

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


@Composable
fun UserScreenDesign(navController: NavController, user_uid: String?) {
    val user_uidInt = user_uid?.toInt()
    var username by remember{ mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var profilePic by remember { mutableStateOf("") }

    LaunchedEffect("test") {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()

            val url = "https://fgehdrx5r6.execute-api.us-east-2.amazonaws.com/wpigroupfinder/signIn"

            val jsonMediaType = "application/json; charset=utf-8".toMediaType()
            println(username)

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
                if (response.isSuccessful) {
                    val jsonResp = JSONObject(response.body?.string())
                    println("Success: ${JSONObject(response.body?.string())}")
                    username = jsonResp.getString("username")
                    description = jsonResp.getString("description")
                } else {
                    println("Error: ${response.code}")
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
            Text("WPI Group Finder User Page")
            Text("$user_uidInt")
            Text("$username")
            Text("$description")
            AsyncImage(
                model = "https://cdn.pixabay.com/photo/2014/06/03/19/38/board-361516_1280.jpg",
                contentDescription = "test image"
            )
            Text("Clubs go here")
        }
    }

}
