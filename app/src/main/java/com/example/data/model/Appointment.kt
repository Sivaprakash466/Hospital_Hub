package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientName: String,
    val patientPhone: String,
    val doctorName: String,
    val department: String,
    val appointmentDate: Long = System.currentTimeMillis(),
    val timeSlot: String, // e.g. "10:30 AM"
    val reason: String,
    val urgency: String = "Normal", // Normal, Urgent, Follow-up
    val status: String = "Scheduled" // Scheduled, Completed, Cancelled
)
