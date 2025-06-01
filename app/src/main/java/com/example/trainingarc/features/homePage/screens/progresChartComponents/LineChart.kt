package com.example.trainingarc.features.homePage.screens.progresChartComponents

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainingarc.features.homePage.viewmodel.ChartEntry

@Composable
fun LineChart(
    data: List<ChartEntry>,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    if (data.isEmpty()) return

    val maxScore = data.maxOf { it.score }.toFloat()
    val minScore = data.minOf { it.score }.toFloat()
    val range = maxScore - minScore

    val labelTextSize = 12.sp
    val labelPaint = android.graphics.Paint().apply {
        color = colorScheme.onSurface.copy(alpha = 0.7f).toArgb()
        textSize = with(LocalDensity.current) { labelTextSize.toPx() }
        textAlign = android.graphics.Paint.Align.RIGHT
    }

    val yAxisLabels = listOf(minScore, (minScore + maxScore) / 2f, maxScore)
    val maxLabelWidth = yAxisLabels.maxOf { labelPaint.measureText("%.1f".format(it)) }
    val yLabelWidth = maxLabelWidth + with(LocalDensity.current) { 8.dp.toPx() }

    val verticalPadding = if (range > 0) range * 0.2f else maxScore * 0.2f
    val bottomPadding = with(LocalDensity.current) { 32.dp.toPx() }
    val topPadding = with(LocalDensity.current) { 16.dp.toPx() }
    val chartHorizontalPadding = with(LocalDensity.current) { 16.dp.toPx() }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        val density = drawContext.density
        val canvasWidth = size.width
        val canvasHeight = size.height

        val chartStartX = yLabelWidth
        val chartEndX = canvasWidth - chartHorizontalPadding
        val chartWidth = chartEndX - chartStartX
        val chartHeight = canvasHeight - topPadding - bottomPadding

        if (chartWidth <= 0 || chartHeight <= 0) return@Canvas

        val xScale = if (data.size > 1) chartWidth / (data.size - 1) else chartWidth
        val yScale = if (maxScore - minScore + 2 * verticalPadding > 0) {
            chartHeight / (maxScore - minScore + 2 * verticalPadding)
        } else {
            1f
        }

        yAxisLabels.forEach { score ->
            val yPosition = canvasHeight - bottomPadding - (score - minScore + verticalPadding) * yScale
            val boundedY = yPosition.coerceIn(
                topPadding + labelPaint.textSize,
                canvasHeight - bottomPadding - labelPaint.textSize/2
            )

            drawContext.canvas.nativeCanvas.drawText(
                "%.1f".format(score),
                yLabelWidth - with(density) { 8.dp.toPx() },
                boundedY + (labelPaint.textSize / 3),
                labelPaint
            )
        }

        val gridLinePaint = android.graphics.Paint().apply {
            color = colorScheme.onSurface.copy(alpha = 0.1f).toArgb()
            strokeWidth = with(density) { 1.dp.toPx() }
        }

        yAxisLabels.forEach { score ->
            val y = canvasHeight - bottomPadding - (score - minScore + verticalPadding) * yScale
            drawContext.canvas.nativeCanvas.drawLine(
                chartStartX,
                y,
                chartEndX,
                y,
                gridLinePaint
            )
        }

        drawLine(
            start = Offset(chartStartX, canvasHeight - bottomPadding),
            end = Offset(chartEndX, canvasHeight - bottomPadding),
            color = colorScheme.onSurface.copy(alpha = 0.3f),
            strokeWidth = with(density) { 1.dp.toPx() }
        )

        val path = Path().apply {
            val firstX = chartStartX
            val firstY = canvasHeight - bottomPadding - (data[0].score.toFloat() - minScore + verticalPadding) * yScale
            moveTo(firstX, firstY)

            data.forEachIndexed { index, entry ->
                val x = chartStartX + index * xScale
                val y = canvasHeight - bottomPadding - (entry.score.toFloat() - minScore + verticalPadding) * yScale
                lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = colorScheme.primary,
            style = Stroke(width = with(density) { 3.dp.toPx() }, cap = StrokeCap.Round)
        )

        data.forEachIndexed { index, entry ->
            val x = chartStartX + index * xScale
            var y = canvasHeight - bottomPadding - (entry.score.toFloat() - minScore + verticalPadding) * yScale

            y = y.coerceIn(
                topPadding + with(density) { 8.dp.toPx() },
                canvasHeight - bottomPadding - with(density) { 8.dp.toPx() }
            )

            drawCircle(
                color = Color.White,
                radius = with(density) { 8.dp.toPx() },
                center = Offset(x, y),
                style = Stroke(width = with(density) { 2.dp.toPx() })
            )

            drawCircle(
                color = colorScheme.primary,
                radius = with(density) { 6.dp.toPx() },
                center = Offset(x, y)
            )

            if (shouldDrawDateLabel(index, data.size)) {
                val datePaint = android.graphics.Paint().apply {
                    color = colorScheme.onSurface.copy(alpha = 0.8f).toArgb()
                    textSize = with(density) { labelTextSize.toPx() }
                    textAlign = android.graphics.Paint.Align.CENTER
                }

                val dateText = entry.formattedDate
                val dateY = canvasHeight - with(density) { 8.dp.toPx() }

                if (datePaint.measureText(dateText) <= xScale) {
                    drawContext.canvas.nativeCanvas.drawText(
                        dateText,
                        x.coerceIn(chartStartX, chartEndX),
                        dateY,
                        datePaint
                    )
                }
            }
        }
    }
}

private fun shouldDrawDateLabel(index: Int, dataSize: Int): Boolean {
    return when {
        dataSize <= 5 -> true
        dataSize <= 10 -> index % 2 == 0
        else -> index % 3 == 0
    }
}