package com.example.calorificomputervision.data

import android.content.pm.PackageInstaller
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.calorificomputervision.model.DetectedObject
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface DetectedObjectDao {
    @Insert
    suspend fun insert(detectedObject: DetectedObjectEntity)

    @Query("SELECT * FROM detected_objects ORDER BY dateTime DESC")
    fun getAllDetectedObjectsFlow(): Flow<List<DetectedObjectEntity>>

    @Query("SELECT DISTINCT sessionId, dateTime FROM detected_objects ORDER BY dateTime DESC")
    fun getDistinctSessionsFlow(): Flow<List<FoodSessionInfo>>

    @Query("SELECT * FROM detected_objects WHERE sessionId = :sessionId ORDER BY dateTime ASC")
    fun getObjectsForSessionFlow(sessionId: String): Flow<List<DetectedObjectEntity>>
}

data class FoodSessionInfo(
    val sessionId: String,
    val dateTime: Date
)