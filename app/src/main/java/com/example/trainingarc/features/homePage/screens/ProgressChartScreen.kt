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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingarc.features.homePage.model.ExerciseWithId
import com.example.trainingarc.features.homePage.viewmodel.ChartEntry
import com.example.trainingarc.features.homePage.viewmodel.ProgressChartState
import com.example.trainingarc.features.homePage.viewmodel.ProgressChartViewModel

@Composable
fun ProgressChartScreen(
    exercise: ExerciseWithId,
    modifier: Modifier = Modifier
) {
    // Tworzymy ViewModel bez Hilt
    val viewModel: ProgressChartViewModel = remember(exercise) {
        ProgressChartViewModel(exercise)
    }
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier.fillMaxSize(),
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

@Composable
private fun ChartContent(
    data: List<ChartEntry>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Historia progresu",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        LineChart(data = data)
    }
}

@Composable
fun LineChart(
    data: List<ChartEntry>,
    modifier: Modifier = Modifier
) {
    val maxScore = data.maxOfOrNull { it.score } ?: 0f
    val minScore = data.minOfOrNull { it.score } ?: 0f

    Canvas(modifier = modifier
        .fillMaxWidth()
        .height(200.dp)
    ) {
        if (data.size < 2) return@Canvas

        val width = size.width
        val height = size.height
        val stepX = width / (data.size - 1)
        val scaleY = height / (maxScore - minScore)

        // Rysowanie linii
        val path = Path().apply {
            moveTo(0f, height - (data[0].score - minScore) * scaleY)
            data.forEachIndexed { index, entry ->
                val x = index * stepX
                val y = height - (entry.score - minScore) * scaleY
                lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Rysowanie punktów
        data.forEachIndexed { index, entry ->
            val x = index * stepX
            val y = height - (entry.score - minScore) * scaleY
            drawCircle(
                color = Color.Blue,
                radius = 5.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}