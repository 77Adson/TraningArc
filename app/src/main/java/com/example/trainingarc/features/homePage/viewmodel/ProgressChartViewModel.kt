package com.example.trainingarc.features.homePage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingarc.features.homePage.model.ExerciseWithId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ProgressChartViewModel(private val exercise: ExerciseWithId) : ViewModel() {
    private val _uiState = MutableStateFlow<ProgressChartState>(ProgressChartState.Loading)
    val uiState: StateFlow<ProgressChartState> = _uiState

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                if (exercise.history.isEmpty()) {
                    _uiState.value = ProgressChartState.Empty
                    return@launch
                }

                val chartData = exercise.history.entries
                    .sortedBy { it.key }
                    .mapNotNull { (date, score) ->
                        try {
                            val dateObj = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
                            ChartEntry(
                                date = date,
                                score = score,
                                formattedDate = SimpleDateFormat("dd MMM", Locale.getDefault()).format(dateObj)
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }

                _uiState.value = ProgressChartState.Success(chartData)
            } catch (e: Exception) {
                _uiState.value = ProgressChartState.Error("Błąd ładowania danych")
            }
        }
    }
}

sealed class ProgressChartState {
    object Loading : ProgressChartState()
    object Empty : ProgressChartState()
    data class Error(val message: String) : ProgressChartState()
    data class Success(val data: List<ChartEntry>) : ProgressChartState()
}

data class ChartEntry(
    val date: String,
    val score: Float,
    val formattedDate: String
)