package com.example.farmedic_wear.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.MaterialTheme
import com.example.farmedic_wear.receivers.NotificationReceiver
import com.example.farmedic_wear.ui.screens.*
import com.example.farmedic_wear.ui.viewmodel.FarmaMedicViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FarmaMedicViewModel by viewModels {
        FarmaMedicViewModel.Factory(applicationContext)
    }
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called with intent: $intent")
        handleIntent(intent)
        setContent {
            FarmaMedicWatchTheme {
                FarmaMedicWatchApp(viewModel)
            }
        }

        // Simulación de pasos cada segundo
        handler.postDelayed(object : Runnable {
            override fun run() {
                viewModel.updateStepCount(viewModel.stepCount.value + 1)
                Log.d("MainActivity", "Simulated step count: ${viewModel.stepCount.value}")
                handler.postDelayed(this, 1000) // Cada segundo
            }
        }, 1000)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("MainActivity", "onNewIntent called with intent: $intent")
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        Log.d("MainActivity", "Handling intent: $intent")
        val type = intent?.getStringExtra(NotificationReceiver.EXTRA_TYPE)
        val medicationId = intent?.getIntExtra(NotificationReceiver.EXTRA_MEDICATION_ID, -1) ?: -1
        Log.d("MainActivity", "Intent extras - type: $type, medicationId: $medicationId")
        if (type != null) {
            when (type) {
                NotificationReceiver.TYPE_HYDRATION -> {
                    Log.d("MainActivity", "Triggering hydration notification")
                    viewModel.triggerHydrationNotification()
                }
                NotificationReceiver.TYPE_MEDICATION -> {
                    if (medicationId != -1) {
                        Log.d("MainActivity", "Triggering medication notification for ID: $medicationId")
                        viewModel.triggerMedicationNotification(medicationId)
                    } else {
                        Log.w("MainActivity", "Invalid medicationId: $medicationId")
                    }
                }
                else -> Log.w("MainActivity", "Unknown notification type: $type")
            }
        } else {
            Log.w("MainActivity", "No notification type found in intent")
        }
    }

    @Composable
    fun FarmaMedicWatchTheme(content: @Composable () -> Unit) {
        MaterialTheme(
            colors = MaterialTheme.colors.copy(
                primary = Color(0xFF3498DB),
                secondary = Color(0xFF1ABC9C),
                background = Color.Black,
                surface = Color(0xFF2C3E50)
            ),
            content = content
        )
    }

    @Composable
    fun FarmaMedicWatchApp(viewModel: FarmaMedicViewModel) {
        var currentScreen by remember { mutableStateOf("main") }
        var selectedMedicationId by remember { mutableStateOf<Int?>(null) }

        val activeNotification by viewModel.activeNotification.collectAsState()
        Log.d("MainActivity", "Current activeNotification: $activeNotification")
        activeNotification?.let { notification ->
            Log.d("MainActivity", "Showing NotificationScreen for notification: $notification")
            NotificationScreen(
                viewModel = viewModel,
                title = notification.title,
                message = notification.message,
                type = notification.type,
                medicationId = notification.medicationId
            )
            return@FarmaMedicWatchApp
        }

        when (currentScreen) {
            "main" -> {
                MainScreen(
                    viewModel = viewModel,
                    onMedicineClick = { currentScreen = "medicationList" },
                    onWaterClick = { currentScreen = "hydration" },
                    onHistoryClick = { currentScreen = "history" },
                    onSettingsClick = { currentScreen = "hydrationSettings" },
                    onMedicationSettingsClick = { currentScreen = "medicationSettings" },
                    onTestNotificationMed = {
                        Log.d("MainActivity", "Test medication notification triggered")
                        viewModel.triggerMedicationNotification(1)
                    },
                    onTestNotificationWater = {
                        Log.d("MainActivity", "Test hydration notification triggered")
                        viewModel.triggerHydrationNotification()
                    }
                )
            }
            "medicationList" -> {
                MedicationListScreen(
                    viewModel = viewModel,
                    onMedicationClick = { medId ->
                        selectedMedicationId = medId
                        currentScreen = "medication"
                    },
                    onBackClick = { currentScreen = "main" }
                )
            }
            "medication" -> {
                selectedMedicationId?.let { medId ->
                    MedicationScreen(
                        viewModel = viewModel,
                        medicationId = medId,
                        onTakenClick = {
                            viewModel.takeMedication(medId)
                            currentScreen = "main"
                        },
                        onSnoozeClick = {
                            viewModel.snoozeMedication(medId, 15)
                            currentScreen = "main"
                        },
                        onBackClick = { currentScreen = "medicationList" }
                    )
                }
            }
            "hydration" -> {
                HydrationScreen(
                    viewModel = viewModel,
                    onDrankClick = {
                        viewModel.drinkWater()
                        currentScreen = "main"
                    },
                    onSnoozeClick = {
                        viewModel.snoozeHydration(15)
                        currentScreen = "main"
                    },
                    onBackClick = { currentScreen = "main" }
                )
            }
            "history" -> {
                HistoryScreen(
                    viewModel = viewModel,
                    onBackClick = { currentScreen = "main" }
                )
            }
            "hydrationSettings" -> {
                HydrationSettingsScreen(
                    viewModel = viewModel,
                    onSaveClick = { currentScreen = "main" },
                    onBackClick = { currentScreen = "main" }
                )
            }
            "medicationSettings" -> {
                MedicationSettingsScreen(
                    viewModel = viewModel,
                    onSaveClick = { currentScreen = "main" },
                    onBackClick = { currentScreen = "main" }
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}