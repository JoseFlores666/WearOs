package com.example.farmedic_wear.ui.screens

import androidx.compose.foundation.background
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
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.farmedic_wear.ui.components.WatchButton
import com.example.farmedic_wear.ui.viewmodel.FarmaMedicViewModel
import com.example.farmedic_wear.ui.viewmodel.HistoryItem

@Composable
fun HistoryScreen(
    viewModel: FarmaMedicViewModel,
    onBackClick: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isRound = screenWidth == screenHeight

    // Adaptación responsiva
    val horizontalPadding = when {
        isRound && screenWidth < 350.dp -> 20.dp
        isRound -> 24.dp
        else -> 16.dp
    }

    val titleSize = when {
        screenWidth < 350.dp -> 16.sp
        screenWidth < 430.dp -> 18.sp
        else -> 20.sp
    }

    val buttonWidth = when {
        screenWidth < 350.dp -> 100
        screenWidth < 430.dp -> 120
        else -> 140
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Usar ScalingLazyColumn para mejor experiencia en Wear OS
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = horizontalPadding,
                vertical = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Header
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "HISTORIAL",
                        color = Color(0xFF9B59B6),
                        fontSize = titleSize,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Resumen del día si hay datos
            if (viewModel.history.isNotEmpty()) {
                item {
                    HistorySummaryCard(
                        viewModel = viewModel,
                        isRound = isRound,
                        screenWidth = screenWidth
                    )
                }
            }

            // Lista de historial o estado vacío
            if (viewModel.history.isEmpty()) {
                item {
                    EmptyHistoryState(screenWidth = screenWidth)
                }
            } else {
                itemsIndexed(viewModel.history) { index, item ->
                    HistoryItemCard(
                        item = item,
                        isRound = isRound,
                        screenWidth = screenWidth
                    )
                }
            }

            // Botón regresar con espaciado
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp)
                ) {
                    WatchButton(
                        text = "Regresar",
                        icon = "←",
                        backgroundColor = listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                        width = buttonWidth,
                        height = 35,
                        onClick = onBackClick
                    )
                }
            }
        }
    }
}

@Composable
fun HistorySummaryCard(
    viewModel: FarmaMedicViewModel,
    isRound: Boolean,
    screenWidth: androidx.compose.ui.unit.Dp
) {
    val todayEntries = viewModel.history
    val medicationsTaken = todayEntries.count { it.type == "Medicamento" }
    val waterIntakes = todayEntries.count { it.type == "Hidratación" }

    val cardPadding = when {
        isRound && screenWidth < 350.dp -> 8.dp
        else -> 12.dp
    }

    val fontSize = when {
        screenWidth < 350.dp -> 10.sp
        else -> 12.sp
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF9B59B6), Color(0xFF8E44AD))
                )
            )
            .padding(cardPadding)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Resumen de hoy",
                color = Color.White,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💊", fontSize = 16.sp)
                    Text(
                        text = "$medicationsTaken",
                        color = Color.White,
                        fontSize = (fontSize.value + 2).sp,
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
                        text = "$waterIntakes",
                        color = Color.White,
                        fontSize = (fontSize.value + 2).sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "vasos agua",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = (fontSize.value - 1).sp
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyHistoryState(screenWidth: androidx.compose.ui.unit.Dp) {
    val iconSize = when {
        screenWidth < 350.dp -> 32.sp
        else -> 40.sp
    }

    val titleSize = when {
        screenWidth < 350.dp -> 12.sp
        else -> 14.sp
    }

    val descriptionSize = when {
        screenWidth < 350.dp -> 10.sp
        else -> 11.sp
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "📋",
                fontSize = iconSize
            )
            Text(
                text = "Sin actividad aún",
                color = Color(0xFF7F8C8D),
                fontSize = titleSize,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Toma medicinas o bebe agua para ver el historial",
                color = Color(0xFF95A5A6),
                fontSize = descriptionSize,
                textAlign = TextAlign.Center,
                lineHeight = (descriptionSize.value + 2).sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun HistoryItemCard(
    item: HistoryItem,
    isRound: Boolean,
    screenWidth: androidx.compose.ui.unit.Dp
) {
    val entryIcon = when {
        item.type == "Hidratación" -> "💧"
        item.type == "Medicamento" -> "💊"
        else -> "📝"
    }

    val entryColor = when {
        item.type == "Medicamento" || item.type == "Hidratación" -> Color(0xFF27AE60)
        else -> Color(0xFF7F8C8D)
    }

    val cardPadding = when {
        isRound && screenWidth < 350.dp -> 8.dp
        else -> 10.dp
    }

    val fontSize = when {
        screenWidth < 350.dp -> 10.sp
        else -> 11.sp
    }

    val timeSize = when {
        screenWidth < 350.dp -> 8.sp
        else -> 9.sp
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1A1A1A))
            .padding(cardPadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = entryIcon,
                    fontSize = 16.sp
                )

                Column {
                    Text(
                        text = item.description,
                        color = Color.White,
                        fontSize = fontSize,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2
                    )
                    Text(
                        text = item.time,
                        color = entryColor,
                        fontSize = timeSize
                    )
                }
            }

            // Indicador de estado
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(entryColor, RoundedCornerShape(3.dp))
            )
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true, name = "320x320")
@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true, name = "454x454")
@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true, name = "390x390")
@Composable
fun HistoryScreenPreview() {
    MaterialTheme {
        // Preview con datos mock
    }
}