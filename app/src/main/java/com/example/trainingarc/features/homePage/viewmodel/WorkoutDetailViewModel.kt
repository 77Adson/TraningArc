package com.example.trainingarc.features.homePage.viewmodel

import androidx.lifecycle.ViewModel
import com.example.trainingarc.features.homePage.model.WorkoutDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

class WorkoutDetailViewModel : ViewModel() {

    // Na potrzeby przykładu, trzymamy dane lokalnie w MutableStateFlow
    private val _details = MutableStateFlow<Map<String, WorkoutDetail>>(emptyMap())
    val details: StateFlow<Map<String, WorkoutDetail>> = _details.asStateFlow()

    // Pobierz szczegóły treningu jako Flow
    fun getDetail(workoutId: String): Flow<WorkoutDetail> {
        return details.map { it[workoutId] ?: WorkoutDetail(workoutId, "") }
    }

    // Zaktualizuj opis treningu
    fun updateDescription(workoutId: String, description: String) {
        _details.update { currentMap ->
            val updated = currentMap.toMutableMap()
            updated[workoutId] = WorkoutDetail(workoutId, description)
            updated
        }
    }
}
