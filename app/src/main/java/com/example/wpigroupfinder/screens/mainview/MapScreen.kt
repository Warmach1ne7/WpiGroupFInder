package com.example.wpigroupfinder.screens.mainview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavController

import kotlinx.coroutines.tasks.await
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.tasks.CancellationTokenSource


//https://github.com/android/platform-samples/blob/main/samples/location/src/main/java/com/example/platform/location/currentLocation/CurrentLocationScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreenDesign(navController: NavController, userid: String?) {
    val context = LocalContext.current
    val mapV = remember { MapView(context) }
    var googleMapInstance by remember { mutableStateOf<GoogleMap?>(null) }

    var isLoading by remember { mutableStateOf(true) }

    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var userLocation by remember { mutableStateOf<LatLng>((LatLng(53.837166, -9.351538))) }
    val locationText = remember { mutableStateOf("Fetching location...") }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            val loc = locationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token
            ).await()

            loc?.let {
                userLocation = LatLng(it.latitude, it.longitude)
            }
        }
    }

    LaunchedEffect(userLocation) {
        googleMapInstance?.let { googleMap ->
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(userLocation).title("You are here"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
        }
        isLoading = false
    }

    LaunchedEffect(userLocation) {
        val geocoder = Geocoder(context)
        try {
            @Suppress("DEPRECATION")
            val address = geocoder.getFromLocation(userLocation.latitude, userLocation.longitude, 1)?.firstOrNull()
            locationText.value = address?.getAddressLine(0) ?: "Address not found"
        } catch (e: Exception) {
            e.printStackTrace()
            locationText.value = "Geocoding error"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Map") },
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AndroidView(
                        factory = { mapV },
                        modifier = Modifier
                            .size(300.dp)
                            .background(Color.Gray, shape = RoundedCornerShape(8.dp)),
                        update = { map ->
                            map.onCreate(Bundle())
                            map.getMapAsync { googleMap ->
                                googleMapInstance = googleMap
                                googleMap.addMarker(
                                    MarkerOptions().position(userLocation).title("Default marker")
                                )
                                googleMap.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        userLocation,
                                        10f
                                    )
                                )
                            }
                            map.onResume()
                        }
                    )
                    Text(
                        text = locationText.value,
                        modifier = Modifier
                            .padding(top = 16.dp),
                        color = Color.White
                    )


                }
            }
        }
    }


    //https://developers.google.com/maps/documentation/android-sdk/map


}

