package com.example.farmedic_wear.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text

@Composable
fun WatchButton(
    text: String,
    icon: String = "",
    backgroundColor: List<Color> = listOf(Color(0xFF3498DB), Color(0xFF2980B9)),
    width: Int = 140,
    height: Int = 40,
    onClick: () -> Unit = {} // Valor por defecto para evitar errores
) {
    Box(
        modifier = Modifier
            .width(width.dp)
            .height(height.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.horizontalGradient(backgroundColor)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon.isNotEmpty()) {
                Text(
                    text = icon,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(end = if (text.isNotEmpty()) 4.dp else 0.dp)
                )
            }

            if (text.isNotEmpty()) {
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = when {
                        width < 70 -> 10.sp
                        width < 100 -> 11.sp
                        else -> 12.sp
                    },
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}