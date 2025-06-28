package com.example.farmedic_wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.farmedic_wear.ui.components.WatchButton
import com.example.farmedic_wear.ui.viewmodel.FarmaMedicViewModel

@Composable
fun HydrationSettingsScreen(
    viewModel: FarmaMedicViewModel,
    onSaveClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val scrollState = rememberScrollState()

    var selectedGoal by remember { mutableStateOf(viewModel.hydrationState.goal.toFloat() / 4) } // Litros (vasos de 250ml)
    var useCustomFrequency by remember { mutableStateOf(viewModel.hydrationState.customFrequency != null) }
    var customFrequency by remember { mutableStateOf(viewModel.hydrationState.customFrequency ?: 2f) } // Horas
    var remindersEnabled by remember { mutableStateOf(viewModel.hydrationState.remindersEnabled) }

    val titleSize = if (screenWidth < 350.dp) 18.sp else 22.sp
    val buttonWidth = if (screenWidth < 350.dp) 130 else 140

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CONFIGURAR HIDRATACIÓN",
            color = Color(0xFF3498DB),
            fontSize = titleSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Meta diaria
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1A1A1A))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Meta diaria",
                    color = Color(0xFFBDC3C7),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WatchButton(
                        text = "2L",
                        width = 60,
                        height = 35,
                        backgroundColor = if (selectedGoal == 2f) listOf(Color(0xFF27AE60), Color(0xFF229954))
                        else listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                        onClick = { selectedGoal = 2f }
                    )
                    WatchButton(
                        text = "3L",
                        width = 60,
                        height = 35,
                        backgroundColor = if (selectedGoal == 3f) listOf(Color(0xFF27AE60), Color(0xFF229954))
                        else listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                        onClick = { selectedGoal = 3f }
                    )
                    WatchButton(
                        text = "5L",
                        width = 60,
                        height = 35,
                        backgroundColor = if (selectedGoal == 5f) listOf(Color(0xFF27AE60), Color(0xFF229954))
                        else listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                        onClick = { selectedGoal = 5f }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Frecuencia
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1A1A1A))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Frecuencia de recordatorios",
                    color = Color(0xFFBDC3C7),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                WatchButton(
                    text = if (useCustomFrequency) "Frecuencia personalizada" else "Frecuencia automática",
                    width = buttonWidth,
                    height = 35,
                    onClick = { useCustomFrequency = !useCustomFrequency }
                )
                if (useCustomFrequency) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WatchButton(
                            text = "10s",
                            width = 50,
                            height = 30,
                            backgroundColor = if (customFrequency == 0.00278f) listOf(Color(0xFF27AE60), Color(0xFF229954))
                            else listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                            onClick = { customFrequency = 0.00278f } // 10 segundos = 10/3600 horas
                        )
                        WatchButton(
                            text = "1h",
                            width = 50,
                            height = 30,
                            backgroundColor = if (customFrequency == 1f) listOf(Color(0xFF27AE60), Color(0xFF229954))
                            else listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                            onClick = { customFrequency = 1f }
                        )
                        WatchButton(
                            text = "2h",
                            width = 50,
                            height = 30,
                            backgroundColor = if (customFrequency == 2f) listOf(Color(0xFF27AE60), Color(0xFF229954))
                            else listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                            onClick = { customFrequency = 2f }
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cada ${(16f / (selectedGoal * 4)).let { if (it.isFinite()) String.format("%.1f", it) else "N/A" }} horas",
                        color = Color(0xFF95A5A6),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Activar/desactivar recordatorios
        WatchButton(
            text = if (remindersEnabled) "Desactivar recordatorios" else "Activar recordatorios",
            width = buttonWidth,
            height = 35,
            backgroundColor = listOf(Color(0xFFF39C12), Color(0xFFE67E22)),
            onClick = { remindersEnabled = !remindersEnabled }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botones de acción
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WatchButton(
                text = "Guardar",
                icon = "✅",
                width = buttonWidth / 2,
                height = 35,
                backgroundColor = listOf(Color(0xFF27AE60), Color(0xFF229954)),
                onClick = {
                    viewModel.updateHydrationSettings(
                        goal = (selectedGoal * 4).toInt(), // Convertir litros a vasos (250ml)
                        customFrequency = if (useCustomFrequency) customFrequency else null,
                        remindersEnabled = remindersEnabled
                    )
                    onSaveClick()
                }
            )
            WatchButton(
                text = "Regresar",
                icon = "←",
                width = buttonWidth / 2,
                height = 35,
                backgroundColor = listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                onClick = onBackClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}