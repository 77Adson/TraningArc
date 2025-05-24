package com.example.trainingarc.features.homePage.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trainingarc.features.homePage.model.ExerciseHistoryEntry
import com.example.trainingarc.features.homePage.viewmodel.ExerciseViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import java.util.Locale
import kotlin.math.max
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressChartScreen(
    exerciseId: String,  // This is now used in LaunchedEffect
    navController: NavController,
    viewModel: ExerciseViewModel = viewModel()
) {
    val detailState by viewModel.detail.collectAsState()
    val density = LocalDensity.current

    // Add this to load data when screen opens
    LaunchedEffect(exerciseId) {
        viewModel.getDetail(exerciseId)
    }

    Scaffold(
        topBar = {
            TopAppBar( // Now properly opted-in
                title = { Text("Progress Chart") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            detailState?.let { exercise ->
                val historyList = exercise.getHistoryList()
                if (historyList.isNotEmpty()) {
                    ExerciseScoreChart(
                        history = historyList,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                } else {
                    Text("No history data available")
                }
            } ?: Text("Loading data...")
        }
    }
}

@Composable
fun ExerciseScoreChart(
    history: List<ExerciseHistoryEntry>,
    modifier: Modifier = Modifier
) {
    val sortedHistory = history.sortedBy { it.timestamp }
    val scores = sortedHistory.map { it.score }
    val maxScore = scores.maxOrNull() ?: 1.0
    val minScore = scores.minOrNull() ?: 0.0
    val dateFormat = remember { SimpleDateFormat("dd MMM", Locale.getDefault()) }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 16.dp.toPx()

        // Oblicz pozycje punktów
        val points = sortedHistory.mapIndexed { index, entry ->
            val x = padding + (width - 2 * padding) * index / (sortedHistory.size - 1).coerceAtLeast(1)
            val y = height - padding - (entry.score - minScore) / (maxScore - minScore).coerceAtLeast(1.0) * (height - 2 * padding)
            Offset(x.toFloat(), y.toFloat())
        }

        // Rysuj linię wykresu
        drawPath(
            path = Path().apply {
                points.forEachIndexed { index, point ->
                    if (index == 0) moveTo(point.x, point.y)
                    else lineTo(point.x, point.y)
                }
            },
            color = Color.Blue,
            style = Stroke(width = 4f)
        )

        // Rysuj punkty danych
        points.forEach { point ->
            drawCircle(
                color = Color.Red,
                radius = 8f,
                center = point
            )
        }

        // Rysuj osie i etykiety
        drawContext.canvas.nativeCanvas.apply {
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 24f
            }

            // Etykieta maksymalnego score
            drawText(
                "%.0f".format(maxScore),
                0f,
                padding + 20,
                paint
            )

            // Etykiety dat
            sortedHistory.forEachIndexed { index, entry ->
                if (index % 2 == 0 || index == sortedHistory.size - 1) {
                    drawText(
                        dateFormat.format(Date(entry.timestamp)),
                        points[index].x - 20,
                        height - 5,
                        paint
                    )
                }
            }
        }
    }
}
