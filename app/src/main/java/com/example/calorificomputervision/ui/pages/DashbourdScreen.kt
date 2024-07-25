package com.example.calorificomputervision.ui.pages

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calorificomputervision.data.AppDatabase
import com.example.calorificomputervision.ui.components.ReusableNavigationBar
import com.example.calorificomputervision.viewmodel.CameraViewModelFactory
import com.example.calorificomputervision.viewmodel.HistoryViewModel

@Composable
fun DashbourdScreen(
    username: String,
    onLogout: () -> Unit,
    onTakePhoto: () -> Unit,
    onSeeHistory: () -> Unit,
    historyViewModel: HistoryViewModel
) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Home", "Calories History")

    Scaffold(
        bottomBar = {
            ReusableNavigationBar(
                selecetedItem = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.height(16.dp))
            when (selectedItem) {
                0 -> {
                    HomeContent(onTakePhoto, onLogout, username)
                }
                1 -> {
                    HistoryScreen(viewModel = historyViewModel)
                }
            }
        }
    }
}

@Composable
fun HomeContent(onTakePhoto: () -> Unit, onLogout: () -> Unit, username: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hey, $username",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onTakePhoto) {
            Text("Take a photo of food")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLogout) {
            Text("Logout")
        }
    }
}