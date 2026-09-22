package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doctors")
data class Doctor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val specialty: String,
    val department: String,
    val qualification: String,
    val roomNumber: String,
    val phone: String,
    val status: String = "On Duty", // "On Duty", "In Surgery", "Off Duty"
    val experienceYears: Int = 10,
    val activePatientsCount: Int = 0
)
