package com.example.trainingarc.features.homePage.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trainingarc.features.homePage.model.ExerciseDetail
import com.example.trainingarc.features.homePage.model.ExerciseHistoryEntry
import com.example.trainingarc.features.homePage.viewmodel.ExerciseViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import com.example.trainingarc.navigation.Routes
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    workoutId: String,
    navController: NavController,
    viewModel: ExerciseViewModel = viewModel()
) {
    val detailState by viewModel.detail.collectAsState()
    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }

    // Dodane deklaracje dla Snackbar i CoroutineScope
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(workoutId) {
        viewModel.getDetail(workoutId)
    }

    LaunchedEffect(detailState) {
        detailState?.let {
            weight = it.weight.toString()
            reps = it.reps.toString()
            sets = it.sets.toString()
            notes = it.notes ?: ""
        } ?: run {
            weight = ""
            reps = ""
            sets = ""
            notes = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły ćwiczenia") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Zamiast ręcznego stringa
                        navController.navigate(Routes.ProgressChart.createRoute(detailState?.workoutId ?: ""))
                    }) {
                        Icon(Icons.Default.ShowChart, contentDescription = "Progress Chart")
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
            if (isEditing) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Waga (kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it.filter { c -> c.isDigit() } },
                    label = { Text("Powtórzenia") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = sets,
                    onValueChange = { sets = it.filter { c -> c.isDigit() } },
                    label = { Text("Serie") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notatka") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Row {
                    Button(onClick = {
                        try {
                            val updatedDetail = ExerciseDetail(
                                workoutId = workoutId,
                                weight = weight.toDoubleOrNull() ?: 0.0,
                                reps = reps.toIntOrNull() ?: 0,
                                sets = sets.toIntOrNull() ?: 0,
                                notes = notes.ifEmpty { null }
                            )
                            viewModel.updateExerciseDetail(updatedDetail)
                            isEditing = false
                        } catch (e: Exception) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Błąd podczas zapisu: ${e.message}")
                            }
                        }
                    }) {
                        Text("Zapisz zmiany")
                    }

                    Spacer(Modifier.width(8.dp))

                    OutlinedButton(onClick = {
                        detailState?.let {
                            weight = it.weight.toString()
                            reps = it.reps.toString()
                            sets = it.sets.toString()
                            notes = it.notes ?: ""
                        }
                        isEditing = false
                    }) {
                        Text("Anuluj")
                    }
                }
            } else {
                Text("Waga: ${detailState?.weight?.toString() ?: "-"} kg")
                Text("Powtórzenia: ${detailState?.reps?.toString() ?: "-"}")
                Text("Serie: ${detailState?.sets?.toString() ?: "-"}")
                Text("Notatka: ${detailState?.notes ?: "-"}")

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { isEditing = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edytuj ćwiczenie")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val weightValue = weight.toDoubleOrNull() ?: 0.0
                                val repsValue = reps.toIntOrNull() ?: 0
                                val setsValue = sets.toIntOrNull() ?: 0

                                // Dodaj lepszą walidację
                                if (weightValue <= 0) {
                                    snackbarHostState.showSnackbar("Waga musi być większa od zera")
                                    return@launch
                                }
                                if (repsValue <= 0) {
                                    snackbarHostState.showSnackbar("Liczba powtórzeń musi być większa od zera")
                                    return@launch
                                }
                                if (setsValue <= 0) {
                                    snackbarHostState.showSnackbar("Liczba serii musi być większa od zera")
                                    return@launch
                                }

                                val entry = ExerciseHistoryEntry(
                                    weight = weightValue,
                                    reps = repsValue,
                                    sets = setsValue,
                                    notes = notes.takeIf { it.isNotBlank() }
                                )

                                viewModel.addProgressEntry(entry)
                                snackbarHostState.showSnackbar("Dodano wynik: ${entry.score.toInt()}")

                                // Resetuj pola po dodaniu
                                weight = ""
                                reps = ""
                                sets = ""
                                notes = ""

                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Błąd: ${e.localizedMessage ?: "Nieznany błąd"}")
                                Log.e("ExerciseDetail", "Error adding entry", e)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = weight.isNotBlank() && reps.isNotBlank() && sets.isNotBlank()
                ) {
                    Text("Dodaj wynik treningu")
                }

                // Wyświetlanie historii
                val history = detailState?.getHistoryList().orEmpty()
                if (history.isNotEmpty()) {
                    Text("Historia:", style = MaterialTheme.typography.titleMedium)
                    history.sortedByDescending { it.timestamp }.forEach { entry ->
                        val date = Date(entry.timestamp)
                        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                        ListItem(
                            headlineContent = {
                                Text("${entry.weight} kg × ${entry.reps} reps (${entry.sets} sets)")
                            },
                            supportingContent = {
                                Text(dateFormat.format(date))
                            },
                            trailingContent = {
                                if (!entry.notes.isNullOrEmpty()) {
                                    Icon(Icons.Default.Notes, contentDescription = "Has notes")
                                }
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}