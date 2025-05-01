package com.example.wpigroupfinder.screens.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@Composable
fun LoginScreenDesign(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var user_uid by remember { mutableStateOf<Int?>(null) }

    fun signInRequest() {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()

            val url = "https://fgehdrx5r6.execute-api.us-east-2.amazonaws.com/wpigroupfinder/signIn"

            val jsonMediaType = "application/json; charset=utf-8".toMediaType()
            println(username)
            println(password)
            val jsonBody = """
            {
                "username": "$username",
                "password": "$password"
            }
        """.trimIndent().toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(url)
                .post(jsonBody)
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBodyString = response.body?.string()
                    println(responseBodyString)
                    val jsonObject = JSONObject(responseBodyString)
                    val body = jsonObject.getJSONObject("body")
                    val user = body.getJSONObject("user")

                    withContext(Dispatchers.Main) {
                        user_uid = user.getString("user_uid").toInt()
                        navController.navigate("events/${user_uid}")
                    }
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

            Text("Back to Event Feed")
            Text("WPI Group Finder Sign In")
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") }
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") }
            )
            Button(onClick = { signInRequest() }) {
                Text("Login")
            }
            Button(onClick = { navController.navigate("faceRecog") }) {
                Text("Sign Up")
            }



        }
    }
}
