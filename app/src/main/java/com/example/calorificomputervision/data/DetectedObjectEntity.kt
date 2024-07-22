package com.example.calorificomputervision.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "detected_objects")
data class DetectedObjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val volumeCm3: Double,
    val massGrams: Double,
    val calories: Double,
    val dateTime: Date,
    val sessionId: String // This will group objects from the same photo session
)