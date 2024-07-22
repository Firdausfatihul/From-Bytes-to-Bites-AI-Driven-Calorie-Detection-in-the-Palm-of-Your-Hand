package com.example.calorificomputervision.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.calorificomputervision.data.DetectedObjectDao

class CameraViewModelFactory(private val detectedObjectDao: DetectedObjectDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CameraViewModel(detectedObjectDao) as T
        } else if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(detectedObjectDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}