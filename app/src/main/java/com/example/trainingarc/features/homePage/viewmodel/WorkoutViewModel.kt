package com.example.trainingarc.features.homePage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.trainingarc.features.homePage.model.Workout


class WorkoutViewModel : ViewModel() {

    // Mapa treningów: sessionId -> lista Workoutów (jako Flow)
    private val workoutMap = mutableMapOf<String, MutableStateFlow<List<Workout>>>()

    fun getWorkouts(sessionId: String): Flow<List<Workout>> {
        return workoutMap.getOrPut(sessionId) { MutableStateFlow(emptyList()) }
    }

    fun addWorkout(sessionId: String, name: String) {
        viewModelScope.launch {
            val listFlow = workoutMap.getOrPut(sessionId) { MutableStateFlow(emptyList()) }
            val newWorkout = Workout(id = System.currentTimeMillis().toString(), name = name)
            listFlow.value = listFlow.value + newWorkout
        }
    }
}
