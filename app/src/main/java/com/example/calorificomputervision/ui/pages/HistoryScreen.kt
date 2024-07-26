package com.example.calorificomputervision.ui.pages

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calorificomputervision.data.FoodSessionInfo
import com.example.calorificomputervision.ui.utils.NeuromorphicShadowModifier
import com.example.calorificomputervision.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.ChartEntryModel

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val sessions by viewModel.sessions.collectAsState(initial = emptyList())
    var selectedSession by remember { mutableStateOf<FoodSessionInfo?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Calorie Intake",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CaloriesSummaryChart(viewModel.dummyChartData)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Food History",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn {
            items(sessions) { session ->
                SessionCard(
                    session = session,
                    viewModel = viewModel,
                    isExpanded = session == selectedSession,
                    onCardClick = {
                        selectedSession = if (selectedSession == session) null else session
                    }
                )
            }
        }
    }
}

@Composable
fun CaloriesSummaryChart(chartData: List<HistoryViewModel.ChartDataPoint>) {
    val chartEntryModel = entriesFromChartData(chartData)

    ProvideChartStyle {
        Chart(
            chart = lineChart(),
            model = chartEntryModel,
            startAxis = startAxis(
                valueFormatter = { value, _ -> "${value.toInt()} cal" }
            ),
            bottomAxis = bottomAxis(
                valueFormatter = { value, _ ->
                    val date = chartData[value.toInt()].date
                    SimpleDateFormat("EEE", Locale.getDefault()).format(date)
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .then(NeuromorphicShadowModifier())
        )
    }
}

fun entriesFromChartData(chartData: List<HistoryViewModel.ChartDataPoint>): ChartEntryModel {
    return entryModelOf(*chartData.mapIndexed { index, point ->
        index.toFloat() to point.calories
    }.toTypedArray())
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SessionCard(
    session: FoodSessionInfo,
    viewModel: HistoryViewModel,
    isExpanded: Boolean,
    onCardClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMMM dd, yyyy - HH:mm", Locale.getDefault())
    val objects by viewModel.getObjectsForSession(session.sessionId).collectAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .then(NeuromorphicShadowModifier())
            .clickable(onClick = onCardClick)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = dateFormat.format(session.dateTime),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    objects.forEach { foodItem ->
                        Text(
                            text = foodItem.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Black
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }
}