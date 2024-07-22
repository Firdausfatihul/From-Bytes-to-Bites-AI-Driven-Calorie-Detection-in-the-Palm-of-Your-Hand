package com.example.calorificomputervision.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorificomputervision.data.DetectedObjectDao
import com.example.calorificomputervision.data.DetectedObjectEntity
import com.example.calorificomputervision.model.DetectedObject
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class CameraViewModel(private val detectedObjectDao: DetectedObjectDao) : ViewModel() {

    fun saveObjects(objects: List<DetectedObject>) {
        viewModelScope.launch {
            val sessionId = UUID.randomUUID().toString()
            val currentTime = Date()

            objects.forEach { obj ->
                detectedObjectDao.insert(
                    DetectedObjectEntity(
                        name = obj.name,
                        volumeCm3 = obj.volumeCm3,
                        massGrams = obj.massGrams,
                        calories = obj.calories,
                        dateTime = currentTime,
                        sessionId = sessionId
                    )
                )
            }
        }
    }
}