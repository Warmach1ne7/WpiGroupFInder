package com.example.wpigroupfinder.ui

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreenDesign(navController: NavController) {
    // State holders for form fields
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf<LocalTime?>(null) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var endTime by remember { mutableStateOf<LocalTime?>(null) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Event") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Event Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Date Picker
            OutlinedTextField(
                value = date?.format(dateFormatter) ?: "",
                onValueChange = {},
                label = { Text("Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                enabled = false,
                readOnly = true
            )
            if (showDatePicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val millis = datePickerState.selectedDateMillis
                                if (millis != null) {
                                    date = Instant.ofEpochMilli(millis)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                }
                                showDatePicker = false
                            }
                        ) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Start Time Picker
            OutlinedTextField(
                value = startTime?.format(timeFormatter) ?: "",
                onValueChange = {},
                label = { Text("Start Time") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showStartTimePicker = true },
                enabled = false,
                readOnly = true
            )
            if (showStartTimePicker) {
                LaunchedEffect(Unit) {
                    val now = LocalTime.now()
                    TimePickerDialog(
                        context,
                        { _, hour: Int, minute: Int ->
                            startTime = LocalTime.of(hour, minute)
                            showStartTimePicker = false
                        },
                        startTime?.hour ?: now.hour,
                        startTime?.minute ?: now.minute,
                        false
                    ).apply {
                        setOnCancelListener { showStartTimePicker = false }
                    }.show()
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // End Time Picker
            OutlinedTextField(
                value = endTime?.format(timeFormatter) ?: "",
                onValueChange = {},
                label = { Text("End Time") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showEndTimePicker = true },
                enabled = false,
                readOnly = true
            )
            if (showEndTimePicker) {
                LaunchedEffect(Unit) {
                    val now = LocalTime.now()
                    TimePickerDialog(
                        context,
                        { _, hour: Int, minute: Int ->
                            endTime = LocalTime.of(hour, minute)
                            showEndTimePicker = false
                        },
                        endTime?.hour ?: now.hour,
                        endTime?.minute ?: now.minute,
                        false
                    ).apply {
                        setOnCancelListener { showEndTimePicker = false }
                    }.show()
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Here you'd add logic to create/save the event
                    showSuccess = true
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && date != null && startTime != null && endTime != null && location.isNotBlank()
            ) {
                Text("Create Event")
            }

            if (showSuccess) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Event created successfully!",
                    color = MaterialTheme.colorScheme.primary
                )
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(1000)
                    navController.popBackStack()
                }
            }
        }
    }
}
