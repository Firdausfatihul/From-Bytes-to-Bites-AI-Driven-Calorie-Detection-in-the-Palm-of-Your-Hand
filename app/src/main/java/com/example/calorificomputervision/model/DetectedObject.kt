package com.example.calorificomputervision.model

data class DetectedObject(
    val name: String,
    val volumeCm3: Double,
    val massGrams: Double,
    val calories: Double
)