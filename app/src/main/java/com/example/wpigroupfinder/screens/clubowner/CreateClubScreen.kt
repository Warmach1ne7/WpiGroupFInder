package com.example.wpigroupfinder.screens.clubowner

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateClubScreenDesign(navController: NavController, userid: String?) {
    var clubname by remember { mutableStateOf("") }
    var clubdesc by remember { mutableStateOf("") }
    val user_uidInt = userid?.toInt()

    var isLoading by remember { mutableStateOf(false) }

    fun createClubRequest(){
        isLoading = true

        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()

            val url = "https://fgehdrx5r6.execute-api.us-east-2.amazonaws.com/wpigroupfinder/createClub"

            val jsonMediaType = "application/json; charset=utf-8".toMediaType()
            println(clubname)
            println(clubdesc)

            val jsonBody = """
            {
                "clubname": "$clubname",
                "clubdesc": "$clubdesc",
                "user_uid": "$user_uidInt"
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
                        val club_uid = body.getString("club_uid")
                        withContext(Dispatchers.Main) {
                            navController.navigate("clubOwner/$club_uid/$user_uidInt")
                        }
                    } else {
                        println("Error: ${it.code}")
                    }
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Club") },
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

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Create Club")
                        OutlinedTextField(
                            value = clubname,
                            onValueChange = { clubname = it },
                            label = { Text("Club Name") }
                        )
                        OutlinedTextField(
                            value = clubdesc,
                            onValueChange = { clubdesc = it },
                            label = { Text("Club Description") }
                        )
                        Button(onClick = { createClubRequest() }) {
                            Text("Create Club")
                        }
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Back")
                        }
                    }
                }
            }
        }
    }
}
