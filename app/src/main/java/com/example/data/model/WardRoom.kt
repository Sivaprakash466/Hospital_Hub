package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wards")
data class WardRoom(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val wardName: String,
    val department: String,
    val floor: String,
    val totalBeds: Int,
    val occupiedBeds: Int,
    val nurseInCharge: String
) {
    val availableBeds: Int get() = (totalBeds - occupiedBeds).coerceAtLeast(0)
    val occupancyPercentage: Float get() = if (totalBeds > 0) (occupiedBeds.toFloat() / totalBeds) else 0f
}
