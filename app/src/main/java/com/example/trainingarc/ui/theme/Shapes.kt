package com.example.trainingarc.ui.theme // Use your actual package name

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
    // You can also define specific shapes like:
    // val pillShape = RoundedCornerShape(percent = 50)
    // val cutTopStart = CutCornerShape(topStart = 16.dp)
)