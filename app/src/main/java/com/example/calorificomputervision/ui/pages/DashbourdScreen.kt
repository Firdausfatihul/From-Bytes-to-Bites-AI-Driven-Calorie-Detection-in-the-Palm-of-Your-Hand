package com.example.calorificomputervision.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashbourdScreen(
    username: String,
    onLogout: () -> Unit,
    onTakePhoto: () -> Unit,
    onSeeHistory: () -> Unit
) {
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
        Button(onClick = onSeeHistory) {
            Text("See History Consumption")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLogout) {
            Text("Logout")
        }
    }
}