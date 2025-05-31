package com.example.trainingarc.ui.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Define your custom shapes as extensions of Shapes
val Shapes.pill: RoundedCornerShape
    get() = RoundedCornerShape(percent = 50)

val Shapes.cutTopStart: CutCornerShape
    get() = CutCornerShape(topStart = 16.dp)

// Create your AppShapes with Material defaults
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)