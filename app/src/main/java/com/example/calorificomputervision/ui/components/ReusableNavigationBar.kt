package com.example.calorificomputervision.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.calorificomputervision.ui.utils.NeuromorphicShadowModifier

@Composable
fun ReusableNavigationBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf("Home", "Calories History")
    val backgroundColor = Color.White
    val shadowColor = Color(0xFFE0E0E0)
    val iconColor = Color.Black

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .then(NeuromorphicShadowModifier())
            .background(backgroundColor)
            .clip(RoundedCornerShape(16.dp)) // Rounded corners
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                label = {
                    Text(
                        item,
                        color = if (selectedItem == index) Color.Black else Color.Gray
                    )
                },
                selected = selectedItem == index,
                onClick = {
                    onItemSelected(index)
                },
                icon = {
                    when (index) {
                        0 -> Icon(Icons.Filled.Home, contentDescription = item, tint = iconColor)
                        1 -> Icon(Icons.Filled.DateRange, contentDescription = item, tint = iconColor)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = iconColor,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = iconColor,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}