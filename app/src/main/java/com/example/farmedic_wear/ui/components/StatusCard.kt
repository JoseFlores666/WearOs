package com.example.farmedic_wear.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

@Composable
fun StatusCard(
    title: String,
    value: String,
    subtitle: String,
    icon: String,
    backgroundColor: List<Color>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(backgroundColor)
            )
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = icon,
                fontSize = 20.sp
            )

            Text(
                text = value,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 8.sp
                )
            }
        }
    }
}

@Composable
fun MedicationStatusCard(
    takenCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    StatusCard(
        title = "Medicinas",
        value = "$takenCount/$totalCount",
        subtitle = "tomadas hoy",
        icon = "💊",
        backgroundColor = listOf(Color(0xFF3498DB), Color(0xFF2980B9)),
        modifier = modifier
    )
}

@Composable
fun HydrationStatusCard(
    lastTime: String,
    status: String,
    modifier: Modifier = Modifier
) {
    val statusColor = when {
        status.contains("Bien") -> listOf(Color(0xFF27AE60), Color(0xFF229954))
        status.contains("Normal") -> listOf(Color(0xFFF39C12), Color(0xFFE67E22))
        else -> listOf(Color(0xFFE74C3C), Color(0xFFC0392B))
    }

    StatusCard(
        title = "Hidratación",
        value = lastTime,
        subtitle = status,
        icon = "💧",
        backgroundColor = statusColor,
        modifier = modifier
    )
}

@Composable
fun ActivityStatusCard(
    totalActivities: Int,
    modifier: Modifier = Modifier
) {
    StatusCard(
        title = "Actividad",
        value = "$totalActivities",
        subtitle = "acciones hoy",
        icon = "📋",
        backgroundColor = listOf(Color(0xFF9B59B6), Color(0xFF8E44AD)),
        modifier = modifier
    )
}

@Preview
@Composable
fun StatusCardPreview() {
    MaterialTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            MedicationStatusCard(
                takenCount = 2,
                totalCount = 3
            )

            HydrationStatusCard(
                lastTime = "14:30",
                status = "Bien hidratado"
            )

            ActivityStatusCard(
                totalActivities = 5
            )
        }
    }
}