package com.example.wpigroupfinder.data.model

data class Event(
    val id: Int? = null,
    val title: String,
    val date: String,
    val location: String,
    val description: String,
    val startTime: String,
    val endTime: String,
    val clubId: Int? = null,
    val createdBy: Int
)