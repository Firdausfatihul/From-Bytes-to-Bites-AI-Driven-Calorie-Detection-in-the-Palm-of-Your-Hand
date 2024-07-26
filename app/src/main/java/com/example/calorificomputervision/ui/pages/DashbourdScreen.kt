package com.example.calorificomputervision.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calorificomputervision.ui.components.ReusableNavigationBar
import com.example.calorificomputervision.ui.utils.NeuromorphicShadowModifier
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
                selectedItem = selectedItem,
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
        ) {
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
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                shadow = Shadow(
                    color = Color.Gray,
                    offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                    blurRadius = 4f
                ),
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        NeuromorphicButton(text = "Take a photo of food", onClick = onTakePhoto)
        Spacer(modifier = Modifier.height(16.dp))
        NeuromorphicButton(text = "Logout", onClick = onLogout)
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun NeuromorphicButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .height(56.dp)
            .then(NeuromorphicShadowModifier()),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                shadow = Shadow(
                    color = Color.Gray,
                    offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                    blurRadius = 4f
                )
            )
        )
    }
}
