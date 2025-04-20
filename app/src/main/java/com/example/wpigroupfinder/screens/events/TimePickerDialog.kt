package com.example.wpigroupfinder.screens.events

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.util.*

@Composable
fun TimePickerField(
    label: String,
    time: String,
    onTimeSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = time,
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        enabled = false,
        readOnly = true
    )

    if (showDialog) {
        ShowTimePickerDialog(
            context = context,
            onTimeSelected = {
                onTimeSelected(it)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun ShowTimePickerDialog(
    context: Context,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    DisposableEffect(Unit) {
        val dialog = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                val formatted = String.format("%02d:%02d", selectedHour, selectedMinute)
                onTimeSelected(formatted)
            },
            hour,
            minute,
            false // Set to true for 24-hour format
        )
        dialog.setOnCancelListener { onDismiss() }
        dialog.show()
        onDispose {
            dialog.dismiss()
        }
    }
}
