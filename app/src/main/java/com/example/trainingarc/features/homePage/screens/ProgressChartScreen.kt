package com.example.trainingarc.features.homePage.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trainingarc.features.homePage.model.ExerciseWithId
import com.example.trainingarc.features.homePage.viewmodel.ChartEntry
import com.example.trainingarc.features.homePage.viewmodel.ProgressChartState
import com.example.trainingarc.features.homePage.viewmodel.ProgressChartViewModel
import com.example.trainingarc.ui.theme.AppSizes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressChartScreen(
    exercise: ExerciseWithId,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val viewModel: ProgressChartViewModel = remember(exercise) {
        ProgressChartViewModel(exercise)
    }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progres: ${exercise.exerciseName}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Wróć"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is ProgressChartState.Loading -> CircularProgressIndicator()
                is ProgressChartState.Empty -> Text("Brak danych historycznych")
                is ProgressChartState.Error -> Text("Błąd: ${state.message}")
                is ProgressChartState.Success -> ChartContent(state.data)
            }
        }
    }
}

@Composable
private fun ChartContent(
    data: List<ChartEntry>,
    modifier: Modifier = Modifier
) {
    val averageScore = if (data.isNotEmpty()) {
        data.map { it.score }.average().toFloat()
    } else {
        0f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Historia progresu",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Statystyki podsumowujące
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatisticItem(
                        label = "Min",
                        value = "${data.minByOrNull { it.score }?.score?.toInt() ?: 0}"
                    )
                    StatisticItem(
                        label = "Max",
                        value = "${data.maxByOrNull { it.score }?.score?.toInt() ?: 0}"
                    )
                    StatisticItem(
                        label = "Średnia",
                        value = "${averageScore.toInt()}"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                LineChart(data = data)
            }
        }
    }
}

@Composable
fun StatisticItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun LineChart(
    data: List<ChartEntry>,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    val maxScore = data.maxOfOrNull { it.score } ?: 0f
    val minScore = data.minOfOrNull { it.score } ?: 0f
    val range = maxScore - minScore
    val padding = if (range > 0) range * 0.1f else 10f // 10% padding

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        if (data.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        val stepX = if (data.size > 1) width / (data.size - 1) else width
        val scaleY = if (maxScore - minScore + 2 * padding > 0) {
            height / (maxScore - minScore + 2 * padding)
        } else { 1f }

        // Rysowanie osi
        drawLine(
            start = Offset(0f, height),
            end = Offset(width, height),
            color = Color.Gray.copy(alpha = 0.5f),
            strokeWidth = 1.dp.toPx()
        )

        // Rysowanie linii progresu
        val path = Path().apply {
            moveTo(0f, height - (data[0].score - minScore + padding) * scaleY)
            data.forEachIndexed { index, entry ->
                val x = index * stepX
                val y = height - (entry.score - minScore + padding) * scaleY
                lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = colorScheme.primary,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Rysowanie punktów
        data.forEachIndexed { index, entry ->
            val x = index * stepX
            val y = height - (entry.score - minScore + padding) * scaleY
            drawCircle(
                color = colorScheme.primary,
                radius = 6.dp.toPx(),
                center = Offset(x, y)
            )

            // Etykiety daty (co 2 punkt lub jeśli mało punktów)
            if (data.size <= 5 || index % 2 == 0) {
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        entry.formattedDate,
                        x,
                        height - 8.dp.toPx(),
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 12.sp.toPx()
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }
        }
    }
}