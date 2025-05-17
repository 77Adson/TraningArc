package com.example.trainingarc.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    // ----- Display Styles (Large headlines at the top of screens) -----
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,  // Default sans-serif
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),  // Use for hero sections (e.g., landing page main headline)

    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),  // Secondary hero text

    // ----- Headline Styles (Page/section titles) -----
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Serif,  // Elegant serif for titles
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),  // Use for screen titles (e.g., "My Profile")

    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),  // Subsection titles (e.g., "Account Settings")

    // ----- Title Styles (Smaller headings) -----
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),  // Card titles, dialog headings

    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,  // System default
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        letterSpacing = 0.15.sp
    ),  // List item headings (e.g., in settings)

    // ----- Body Styles (Main content) -----
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),  // Standard paragraph text

    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),  // Secondary text (e.g., under cards)

    // ----- Label Styles (Buttons, chips, captions) -----
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),  // Button text, prominent labels

    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,  // Fixed-width for precision
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )  // Timestamps, tiny labels (e.g., "NEW")
)