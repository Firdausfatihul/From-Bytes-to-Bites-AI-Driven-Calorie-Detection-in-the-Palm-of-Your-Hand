package com.example.calorificomputervision.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ReusableNavigationBar(
    selecetedItem: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf("Home", "Calories History")

    NavigationBar(
        modifier = modifier
    ) {
        items.forEachIndexed{ index, item ->
            NavigationBarItem(
                label = {
                        Text(item)
                },
                selected = selecetedItem == index,
                onClick = {
                    onItemSelected(index)
                },
                icon = {
                    when (index) {
                        0 -> Icon(Icons.Filled.Home, contentDescription = item)
                        1 -> Icon(Icons.Filled.DateRange, contentDescription = item)
                    }
                }
            )
        }
    }
}