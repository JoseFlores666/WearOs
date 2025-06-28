package com.example.farmedic_wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.farmedic_wear.ui.components.WatchButton
import com.example.farmedic_wear.ui.viewmodel.FarmaMedicViewModel
import com.example.farmedic_wear.ui.viewmodel.NotificationType

@Composable
fun NotificationScreen(
    viewModel: FarmaMedicViewModel,
    title: String,
    message: String,
    type: NotificationType,
    medicationId: Int? = null
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isRound = screenWidth == screenHeight

    val (backgroundColor, icon) = when (type) {
        NotificationType.MEDICATION -> Pair(
            listOf(Color(0xFFE74C3C), Color(0xFFC0392B)),
            "💊"
        )
        NotificationType.HYDRATION -> Pair(
            listOf(Color(0xFF3498DB), Color(0xFF2980B9)),
            "💧"
        )
    }

    val containerSize = when {
        screenWidth < 350.dp -> screenWidth - 20.dp
        else -> screenWidth - 30.dp
    }

    val titleSize = when {
        screenWidth < 350.dp -> 14.sp
        else -> 16.sp
    }

    val messageSize = when {
        screenWidth < 350.dp -> 10.sp
        else -> 12.sp
    }

    val iconSize = when {
        screenWidth < 350.dp -> 32.sp
        else -> 40.sp
    }

    val buttonWidth = when {
        screenWidth < 350.dp -> 90
        else -> 110
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(containerSize)
                .clip(if (isRound) CircleShape else RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = backgroundColor
                    )
                )
        ) {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    Text(
                        text = "⏰",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                item {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = titleSize,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = titleSize * 1.1,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                item {
                    Text(
                        text = icon,
                        fontSize = iconSize,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    Text(
                        text = message,
                        color = Color.White.copy(alpha = 0.95f),
                        fontSize = messageSize,
                        textAlign = TextAlign.Center,
                        lineHeight = messageSize * 1.2,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                when (type) {
                    NotificationType.HYDRATION -> {
                        item {
                            WatchButton(
                                text = "Ya bebí",
                                icon = "✅",
                                backgroundColor = listOf(
                                    Color(0xFF27AE60),
                                    Color(0xFF229954)
                                ),
                                width = buttonWidth,
                                height = 32,
                                onClick = { viewModel.drinkWater() }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                        item {
                            WatchButton(
                                text = "+15 min",
                                icon = "⏰",
                                backgroundColor = listOf(
                                    Color(0xFFF39C12),
                                    Color(0xFFE67E22)
                                ),
                                width = buttonWidth,
                                height = 32,
                                onClick = { viewModel.snoozeHydration(15) }
                            )
                        }
                    }
                    NotificationType.MEDICATION -> {
                        if (medicationId == null) return@ScalingLazyColumn // Evita crash si medicationId es nulo
                        item {
                            WatchButton(
                                text = "Lo tomé",
                                icon = "✅",
                                backgroundColor = listOf(
                                    Color(0xFF27AE60),
                                    Color(0xFF229954)
                                ),
                                width = buttonWidth,
                                height = 32,
                                onClick = { viewModel.takeMedication(medicationId) }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                        item {
                            WatchButton(
                                text = "+15 min",
                                icon = "⏰",
                                backgroundColor = listOf(
                                    Color(0xFFF39C12),
                                    Color(0xFFE67E22)
                                ),
                                width = buttonWidth,
                                height = 32,
                                onClick = { viewModel.snoozeMedication(medicationId, 15) }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                        item {
                            WatchButton(
                                text = "Omitida",
                                icon = "✕",
                                backgroundColor = listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Black.copy(alpha = 0.3f)
                                ),
                                width = buttonWidth - 10,
                                height = 28,
                                onClick = { viewModel.skipMedication(medicationId) }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true, name = "Pequeño 320x320")
@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true, name = "Grande 454x454")
@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true, name = "Cuadrado 390x390")
@Composable
fun NotificationScreenPreview() {
    MaterialTheme {
        // Preview con datos mock
    }
}