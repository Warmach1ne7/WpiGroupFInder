package com.example.wpigroupfinder.screens.clubowner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
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

            val url = "https://fgehdrx5r6.execute-api.us-east-2.amazonaws.com/wpigroupfinder/viewClub" //TODO: switch url

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

            val url = "https://fgehdrx5r6.execute-api.us-east-2.amazonaws.com/wpigroupfinder/joinClub" //TODO: switch url

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

            val url = "https://fgehdrx5r6.execute-api.us-east-2.amazonaws.com/wpigroupfinder/leaveClub" //TODO: switch url

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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if(memberNum != 0) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("$clubName")
                Text("Owner")
                Text("$clubOwnerName")
                Text("About")
                Text("$clubDesc")
                Text("Number of Members")
                Text("$memberNum")

                Button(onClick = { navController.navigate("eventFeed") }) {
                    Text("Back")
                }

                if (clubOwnerUid == userIdInt) { //put in if club owner, this is viewable
                    Button(onClick = { navController.navigate("editClubPage") }) {
                        Text("Edit Club Page")
                    }
                } else if (!isMember){
                    Button(onClick = { joinClub() }) {
                        Text("Join Club")
                    }
                }
                else if(isMember == true && clubOwnerUid != userIdInt){
                    Button(onClick = {leaveClub()}){
                        Text("Leave Club")
                    }
                }
                Button(onClick = { navController.navigate("clubEvents/$clubIdInt") }) {
                    Text("Club Events")
                }
            }
        }
    }

}