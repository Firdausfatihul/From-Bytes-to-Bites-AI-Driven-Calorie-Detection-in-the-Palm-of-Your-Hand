package com.example.calorificomputervision.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorificomputervision.data.DetectedObjectDao
import com.example.calorificomputervision.data.DetectedObjectEntity
import com.example.calorificomputervision.data.FoodSessionInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(private val detectedObjectDao: DetectedObjectDao) : ViewModel() {
    private val _sessions = MutableStateFlow<List<FoodSessionInfo>>(emptyList())
    val sessions: StateFlow<List<FoodSessionInfo>> = _sessions

    init {
        viewModelScope.launch {
            detectedObjectDao.getDistinctSessionsFlow().collect { sessions ->
                _sessions.value = sessions
            }
        }
    }

    fun getObjectsForSession(sessionId: String): Flow<List<DetectedObjectEntity>> {
        return detectedObjectDao.getObjectsForSessionFlow(sessionId)
    }
}