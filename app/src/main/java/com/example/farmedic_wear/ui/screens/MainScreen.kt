package com.example.farmedic_wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
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
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.farmedic_wear.ui.components.WatchButton
import com.example.farmedic_wear.ui.viewmodel.FarmaMedicViewModel

@Composable
fun MainScreen(
    viewModel: FarmaMedicViewModel? = null,
    onMedicineClick: () -> Unit = {},
    onWaterClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onMedicationSettingsClick: () -> Unit = {},
    onTestNotificationMed: () -> Unit = {},
    onTestNotificationWater: () -> Unit = {}
) {
    val stepCount by viewModel?.stepCount?.collectAsState() ?: remember { mutableStateOf(0) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isRound = screenWidth == screenHeight

    val horizontalPadding = when {
        isRound && screenWidth < 350.dp -> 20.dp
        isRound -> 28.dp
        else -> 16.dp
    }

    val titleSize = when {
        screenWidth < 350.dp -> 14.sp
        screenWidth < 430.dp -> 18.sp
        else -> 22.sp
    }

    val buttonWidth = when {
        screenWidth < 350.dp -> 120
        screenWidth < 430.dp -> 140
        else -> 160
    }

    val buttonHeight = when {
        screenWidth < 350.dp -> 38
        screenWidth < 430.dp -> 42
        else -> 45
    }

    val cardPadding = when {
        screenWidth < 350.dp -> 8.dp
        else -> 12.dp
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = horizontalPadding,
                vertical = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "FARMAMEDIC",
                    color = Color(0xFF3498DB),
                    fontSize = titleSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
            }

            viewModel?.let { vm ->
                item {
                    StatusCard(
                        viewModel = vm,
                        isRound = isRound,
                        screenWidth = screenWidth,
                        cardPadding = cardPadding,
                        stepCount = stepCount
                    )
                }
            }

            item {
                WatchButton(
                    text = "Medicinas",
                    icon = "💊",
                    backgroundColor = listOf(
                        Color(0xFF3498DB),
                        Color(0xFF2980B9)
                    ),
                    width = buttonWidth,
                    height = buttonHeight,
                    onClick = onMedicineClick
                )
            }

            item {
                WatchButton(
                    text = "Agregar medicina",
                    icon = "➕",
                    backgroundColor = listOf(
                        Color(0xFF27AE60),
                        Color(0xFF229954)
                    ),
                    width = buttonWidth,
                    height = buttonHeight,
                    onClick = onMedicationSettingsClick
                )
            }

            item {
                WatchButton(
                    text = "Hidratación",
                    icon = "💧",
                    backgroundColor = listOf(
                        Color(0xFF1ABC9C),
                        Color(0xFF16A085)
                    ),
                    width = buttonWidth,
                    height = buttonHeight,
                    onClick = onWaterClick
                )
            }

            item {
                WatchButton(
                    text = "Configurar agua",
                    icon = "⚙️",
                    backgroundColor = listOf(
                        Color(0xFFF39C12),
                        Color(0xFFE67E22)
                    ),
                    width = buttonWidth,
                    height = buttonHeight,
                    onClick = onSettingsClick
                )
            }

            item {
                WatchButton(
                    text = "Historial",
                    icon = "📋",
                    backgroundColor = listOf(
                        Color(0xFF9B59B6),
                        Color(0xFF8E44AD)
                    ),
                    width = buttonWidth,
                    height = buttonHeight,
                    onClick = onHistoryClick
                )
            }

            if (viewModel != null) {
                item {
                    TestButtonsSection(
                        screenWidth = screenWidth,
                        onTestNotificationMed = onTestNotificationMed,
                        onTestNotificationWater = onTestNotificationWater
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun StatusCard(
    viewModel: FarmaMedicViewModel,
    isRound: Boolean,
    screenWidth: androidx.compose.ui.unit.Dp,
    cardPadding: androidx.compose.ui.unit.Dp,
    stepCount: Int
) {
    val fontSize = when {
        screenWidth < 350.dp -> 10.sp
        else -> 12.sp
    }

    val numberSize = when {
        screenWidth < 350.dp -> 12.sp
        else -> 14.sp
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1A1A))
            .padding(cardPadding)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Estado de hoy",
                color = Color(0xFF3498DB),
                fontSize = fontSize,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💊", fontSize = 16.sp)
                    Text(
                        text = "${viewModel.medications.size}",
                        color = Color.White,
                        fontSize = numberSize,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "medicinas",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = (fontSize.value - 1).sp
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💧", fontSize = 16.sp)
                    Text(
                        text = "${(viewModel.todayIntake * 0.25f).let { String.format("%.1f", it) }}/${(viewModel.dailyGoal * 0.25f).let { String.format("%.1f", it) }}L",
                        color = Color.White,
                        fontSize = numberSize,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "agua hoy",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = (fontSize.value - 1).sp
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👣", fontSize = 16.sp)
                    Text(
                        text = "$stepCount",
                        color = Color.White,
                        fontSize = numberSize,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "pasos",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = (fontSize.value - 1).sp
                    )
                }
            }
        }
    }
}

@Composable
fun TestButtonsSection(
    screenWidth: androidx.compose.ui.unit.Dp,
    onTestNotificationMed: () -> Unit,
    onTestNotificationWater: () -> Unit
) {
    val buttonSize = when {
        screenWidth < 350.dp -> 60
        else -> 70
    }

    val textSize = when {
        screenWidth < 350.dp -> 12.sp
        else -> 14.sp
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text(
            text = "Pruebas de notificaciones:",
            color = Color(0xFF95A5A6),
            fontSize = textSize,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WatchButton(
                text = "Med",
                icon = "🔔",
                backgroundColor = listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                width = buttonSize,
                height = 36,
                onClick = onTestNotificationMed
            )

            WatchButton(
                text = "H2O",
                icon = "🔔",
                backgroundColor = listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                width = buttonSize,
                height = 36,
                onClick = onTestNotificationWater
            )
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true, name = "320x320")
@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true, name = "454x454")
@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true, name = "390x390")
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        MainScreen()
    }
}