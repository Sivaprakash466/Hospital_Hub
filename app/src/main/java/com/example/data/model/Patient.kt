package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val age: Int,
    val gender: String, // Male, Female, Other
    val bloodGroup: String, // A+, B+, O+, AB+, A-, B-, O-, AB-
    val phone: String,
    val admissionType: String, // Inpatient, Outpatient, Emergency, ICU
    val department: String, // Cardiology, Neurology, General Medicine, Pediatrics, Orthopedics, Emergency
    val attendingDoctor: String,
    val wardNumber: String,
    val bedNumber: String,
    val diagnosis: String,
    val vitalsBp: String, // e.g. "120/80"
    val vitalsPulse: Int, // e.g. 72
    val vitalsSpO2: Int, // e.g. 98
    val vitalsTemp: Double, // e.g. 98.6
    val status: String, // Admitted, Critical, Stable, Recovering, Discharged
    val admissionDate: Long = System.currentTimeMillis(),
    val notes: String = ""
)
