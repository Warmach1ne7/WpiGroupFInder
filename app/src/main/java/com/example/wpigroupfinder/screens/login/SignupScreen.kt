package com.example.wpigroupfinder.screens.login

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
fun SignupScreenDesign(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var profilePic: Any? =
        imageUri ?: "https://wpigroupfinder.s3.us-east-2.amazonaws.com/images/test_pfp.jpg"
    val context = LocalContext.current
    var launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    var isLoading by remember { mutableStateOf(false) }

    fun uriToByteArray(context: Context, uri: Uri?): ByteArray {
        return context.contentResolver.openInputStream(uri!!)?.readBytes()
            ?: throw IllegalArgumentException("Cannot open input stream from URI")
    }

    fun createUserRequest() {
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()

            val url =
                "https://fgehdrx5r6.execute-api.us-east-2.amazonaws.com/wpigroupfinder/createUser"

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
                    println("Success: ${JSONObject(response.body?.string())}")
                    withContext(Dispatchers.Main) {
                        navController.navigate("login")
                    }
                } else {
                    println("Error: ${response.code}")
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("login") }) {
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
                        Text("Create User")
                        AsyncImage(
                            model = profilePic,
                            contentDescription = "test image",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                        )
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") }
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            visualTransformation = PasswordVisualTransformation(),
                            label = { Text("Password") }
                        )
                        Button(onClick = { createUserRequest() }) {
                            Text("Create User")
                        }
                    }
                }
            }
        }
    }
}
