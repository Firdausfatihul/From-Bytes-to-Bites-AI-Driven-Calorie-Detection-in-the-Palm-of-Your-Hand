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
import java.util.Calendar
import java.util.Date
import kotlin.random.Random

class HistoryViewModel(private val detectedObjectDao: DetectedObjectDao) : ViewModel() {
    private val _sessions = MutableStateFlow<List<FoodSessionInfo>>(emptyList())
    val sessions: StateFlow<List<FoodSessionInfo>> = _sessions

    val dummyChartData = List(7) { index ->
        ChartDataPoint(
            date = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -6 + index) }.time,
            calories = Random.nextDouble(1500.0, 2500.0)
        )
    }

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

    data class ChartDataPoint(val date: Date, val calories: Double)
}