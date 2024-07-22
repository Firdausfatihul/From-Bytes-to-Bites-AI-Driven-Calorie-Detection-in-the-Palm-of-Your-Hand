package com.example.calorificomputervision.ui.pages

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calorificomputervision.data.FoodSessionInfo
import com.example.calorificomputervision.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val sessions by viewModel.sessions.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(sessions) { session ->
            SessionCard(session, viewModel)
        }
    }
}

@Composable
fun SessionCard(session: FoodSessionInfo, viewModel: HistoryViewModel) {
    val dateFormat = SimpleDateFormat("MMMM dd, yyyy - HH:mm", Locale.getDefault())
    val objects by viewModel.getObjectsForSession(session.sessionId).collectAsState(initial = emptyList())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = dateFormat.format(session.dateTime),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            objects.forEach { obj ->
                Text("${obj.name}: ${obj.calories.toInt()} calories")
            }
        }
    }
}