package com.example.farmedic_wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.farmedic_wear.ui.components.WatchButton
import com.example.farmedic_wear.ui.viewmodel.FarmaMedicViewModel

@Composable
fun MedicationScreen(
    viewModel: FarmaMedicViewModel,
    medicationId: Int,
    onTakenClick: () -> Unit = {},
    onSnoozeClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val medication = viewModel.medications.find { it.id == medicationId }

    if (medication == null) {
        // Pantalla de error
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Medicamento no encontrado",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                WatchButton(
                    text = "Regresar",
                    icon = "←",
                    backgroundColor = listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                    onClick = onBackClick
                )
            }
        }
        return
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val scrollState = rememberScrollState()

    val titleSize = when {
        screenWidth < 350.dp -> 18.sp
        else -> 22.sp
    }

    val buttonWidth = when {
        screenWidth < 350.dp -> 130
        else -> 140
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header con nombre del medicamento
        Text(
            text = medication.name,
            color = Color.White,
            fontSize = titleSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Icono del medicamento
        Text(
            text = "💊",
            fontSize = 48.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Card con información detallada
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1A1A1A))
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Dosis
                Text(
                    text = "Dosis: ${medication.dosage}",
                    color = Color(0xFFBDC3C7),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                // Frecuencia basada en los horarios
                val frequency = when (medication.times.size) {
                    1 -> "1 vez al día"
                    2 -> "2 veces al día"
                    3 -> "3 veces al día"
                    else -> "${medication.times.size} veces al día"
                }

                Text(
                    text = frequency,
                    color = Color(0xFF95A5A6),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Horarios programados
                Text(
                    text = "Horarios:",
                    color = Color(0xFF3498DB),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                medication.times.forEach { time ->
                    Text(
                        text = time,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Próxima dosis
                Text(
                    text = "Próxima dosis: ${medication.nextDose}",
                    color = Color(0xFF27AE60),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                // Estado de la próxima dosis
                val (statusText, statusColor) = getMedicationUrgency(medication.nextDose)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(statusColor, RoundedCornerShape(4.dp))
                    )
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mensaje motivacional
        Text(
            text = "\"Cuidar tu salud es una prioridad\"",
            color = Color(0xFFECF0F1),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Botones de acción
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WatchButton(
                text = "Ya tomé",
                icon = "✅",
                backgroundColor = listOf(
                    Color(0xFF27AE60),
                    Color(0xFF229954)
                ),
                width = buttonWidth,
                height = 40,
                onClick = onTakenClick
            )

            WatchButton(
                text = "+15 min",
                icon = "⏰",
                backgroundColor = listOf(
                    Color(0xFFF39C12),
                    Color(0xFFE67E22)
                ),
                width = buttonWidth,
                height = 40,
                onClick = onSnoozeClick
            )

            Spacer(modifier = Modifier.height(8.dp))

            WatchButton(
                text = "Regresar",
                icon = "←",
                backgroundColor = listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                width = 100,
                height = 35,
                onClick = onBackClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun getMedicationUrgency(nextDose: String): Pair<String, Color> {
    // Obtener la hora actual en formato HH:mm
    val currentTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        .format(java.util.Date())

    // Convertir horas a minutos para comparar
    fun timeToMinutes(time: String): Int {
        return try {
            val parts = time.split(":")
            parts[0].toInt() * 60 + parts[1].toInt()
        } catch (e: Exception) {
            0
        }
    }

    val currentMinutes = timeToMinutes(currentTime)
    val nextDoseMinutes = timeToMinutes(nextDose)

    val timeDiff = nextDoseMinutes - currentMinutes

    return when {
        timeDiff <= 0 -> Pair("¡Es hora de tomar!", Color(0xFFE74C3C))
        timeDiff <= 15 -> Pair("Muy pronto", Color(0xFFF39C12))
        timeDiff <= 60 -> Pair("En ${timeDiff} minutos", Color(0xFF3498DB))
        timeDiff <= 180 -> Pair("En ${timeDiff/60}h ${timeDiff%60}min", Color(0xFF27AE60))
        else -> Pair("Programada", Color(0xFF95A5A6))
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun MedicationScreenPreview() {
    MaterialTheme {
        // Preview con datos mock
    }
}