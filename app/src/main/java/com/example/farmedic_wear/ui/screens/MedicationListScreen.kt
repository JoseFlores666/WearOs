package com.example.farmedic_wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.farmedic_wear.ui.viewmodel.Medication

@Composable
fun MedicationListScreen(
    viewModel: FarmaMedicViewModel,
    onMedicationClick: (Int) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val titleSize = when {
        screenWidth < 350.dp -> 18.sp
        else -> 22.sp
    }

    val cardHeight = when {
        screenWidth < 350.dp -> 65.dp
        else -> 75.dp
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "MIS MEDICINAS",
            color = Color(0xFF3498DB),
            fontSize = titleSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // Lista scrolleable de medicamentos
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(viewModel.medications) { medication ->
                MedicationCard(
                    medication = medication,
                    onClick = { onMedicationClick(medication.id) },
                    cardHeight = cardHeight
                )
            }
        }

        // Botón regresar
        Spacer(modifier = Modifier.height(8.dp))
        WatchButton(
            text = "Regresar",
            icon = "←",
            backgroundColor = listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
            width = 120,
            height = 35,
            onClick = onBackClick
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun MedicationCard(
    medication: Medication,
    onClick: () -> Unit,
    cardHeight: androidx.compose.ui.unit.Dp
) {
    // Determinar el estado basado en la próxima dosis
    val (statusText, statusColor) = getMedicationStatus(medication.nextDose)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF2C3E50), Color(0xFF34495E))
                )
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de estado
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(statusColor, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Información del medicamento
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = medication.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${medication.dosage} • ${medication.times.size} vez${if(medication.times.size > 1) "es" else ""}/día",
                    color = Color(0xFFBDC3C7),
                    fontSize = 10.sp
                )
                Text(
                    text = "Próxima: ${medication.nextDose}",
                    color = Color(0xFF95A5A6),
                    fontSize = 9.sp
                )
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Icono de flecha
            Text(
                text = "→",
                color = Color(0xFF7F8C8D),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun getMedicationStatus(nextDose: String): Pair<String, Color> {
    // Obtener la hora actual en formato HH:mm
    val currentTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        .format(java.util.Date())

    // Convertir horas a minutos para comparar
    fun timeToMinutes(time: String): Int {
        val parts = time.split(":")
        return parts[0].toIntOrNull()?.times(60)?.plus(parts[1].toIntOrNull() ?: 0) ?: 0
    }

    val currentMinutes = timeToMinutes(currentTime)
    val nextDoseMinutes = timeToMinutes(nextDose)

    val timeDiff = nextDoseMinutes - currentMinutes

    return when {
        timeDiff <= 0 -> Pair("Es hora de tomar", Color(0xFFE74C3C)) // Rojo - ya es hora
        timeDiff <= 30 -> Pair("Próxima en ${timeDiff}min", Color(0xFFF39C12)) // Naranja - pronto
        timeDiff <= 120 -> Pair("En ${timeDiff/60}h ${timeDiff%60}min", Color(0xFF3498DB)) // Azul - dentro de poco
        else -> Pair("Programada", Color(0xFF27AE60)) // Verde - no urgente
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun MedicationListScreenPreview() {
    MaterialTheme {
        // Preview con datos mock
    }
}