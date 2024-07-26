package com.example.calorificomputervision.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity

@Composable
fun Dp.toPx(): Float {
    return with(LocalDensity.current) { this@toPx.toPx() }
}

@Composable
fun NeuromorphicShadowModifier(
    offsetX: Dp = 4.dp,
    offsetY: Dp = 4.dp,
    blurRadius: Dp = 8.dp,
    spread: Dp = 1.dp,
    lightColor: Color = Color.White,
    darkColor: Color = Color(0xFFB0BEC5)
): Modifier {
    val offsetXPx = offsetX.toPx()
    val offsetYPx = offsetY.toPx()
    val blurRadiusPx = blurRadius.toPx()
    val spreadPx = spread.toPx()

    return Modifier.background(
        brush = Brush.linearGradient(
            colors = listOf(
                lightColor.copy(alpha = 0.7f),
                darkColor.copy(alpha = 0.1f)
            ),
            start = Offset(
                offsetXPx,
                offsetYPx
            ),
            end = Offset(
                offsetXPx + blurRadiusPx,
                offsetYPx + blurRadiusPx
            )
        ),
        shape = RoundedCornerShape(8.dp)
    )
}