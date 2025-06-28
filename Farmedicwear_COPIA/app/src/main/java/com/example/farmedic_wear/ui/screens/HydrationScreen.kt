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
fun HydrationScreen(
    viewModel: FarmaMedicViewModel,
    onDrankClick: () -> Unit = {},
    onSnoozeClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
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
        Text(
            text = "BEBER AGUA",
            color = Color(0xFF3498DB),
            fontSize = titleSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
            text = "💧💧💧",
            fontSize = 36.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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
                val progress = "${(viewModel.todayIntake * 0.25f).let { String.format("%.1f", it) }}/${(viewModel.dailyGoal * 0.25f).let { String.format("%.1f", it) }}L"
                Text(
                    text = "Progreso de hoy: $progress",
                    color = Color(0xFFBDC3C7),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                val hydrationStatus = getHydrationStatus(viewModel.todayIntake, viewModel.dailyGoal)
                Text(
                    text = hydrationStatus.first,
                    color = hydrationStatus.second,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                HydrationProgressBar(
                    current = viewModel.todayIntake,
                    goal = viewModel.dailyGoal
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🎯", fontSize = 12.sp)
                    Text(
                        text = "Meta: ${(viewModel.dailyGoal * 0.25f).let { String.format("%.1f", it) }}L diarios",
                        color = Color(0xFF7F8C8D),
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val motivationalMessage = getMotivationalMessage(viewModel.todayIntake, viewModel.dailyGoal)
        Text(
            text = "\"$motivationalMessage\"",
            color = Color(0xFFECF0F1),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WatchButton(
                text = "Ya bebí",
                icon = "✅",
                backgroundColor = listOf(
                    Color(0xFF27AE60),
                    Color(0xFF229954)
                ),
                width = buttonWidth,
                height = 40,
                onClick = onDrankClick
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
fun HydrationProgressBar(current: Int, goal: Int) {
    val progress = (current.toFloat() / goal.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color(0xFF34495E))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(3.dp))
                .background(
                    when {
                        progress >= 1f -> Color(0xFF27AE60)
                        progress >= 0.6f -> Color(0xFF3498DB)
                        progress >= 0.3f -> Color(0xFFF39C12)
                        else -> Color(0xFFE74C3C)
                    }
                )
        )
    }
}

@Composable
fun getHydrationStatus(current: Int, goal: Int): Pair<String, Color> {
    val percentage = current.toFloat() / goal.toFloat()

    return when {
        percentage >= 1f -> Pair("¡Meta alcanzada!", Color(0xFF27AE60))
        percentage >= 0.75f -> Pair("Muy bien hidratado", Color(0xFF3498DB))
        percentage >= 0.5f -> Pair("Progreso bueno", Color(0xFFF39C12))
        percentage >= 0.25f -> Pair("Necesitas más agua", Color(0xFFE67E22))
        else -> Pair("Debes hidratarte más", Color(0xFFE74C3C))
    }
}

fun getMotivationalMessage(current: Int, goal: Int): String {
    val percentage = current.toFloat() / goal.toFloat()

    return when {
        percentage >= 1f -> "¡Excelente trabajo hoy!"
        percentage >= 0.75f -> "Casi llegas a tu meta"
        percentage >= 0.5f -> "Vas por buen camino"
        percentage >= 0.25f -> "Mantente hidratado"
        else -> "Tu cuerpo necesita agua"
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun HydrationScreenPreview() {
    MaterialTheme {
        // Preview con datos mock
    }
}