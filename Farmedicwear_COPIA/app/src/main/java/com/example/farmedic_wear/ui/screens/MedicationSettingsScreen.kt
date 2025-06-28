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
fun MedicationSettingsScreen(
    viewModel: FarmaMedicViewModel,
    onSaveClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val scrollState = rememberScrollState()

    var selectedMedication by remember { mutableStateOf("") }
    var dosageAmount by remember { mutableStateOf("1") }
    var dosageUnit by remember { mutableStateOf("pastilla(s)") }
    var frequency by remember { mutableStateOf(8f) }
    var remindersEnabled by remember { mutableStateOf(true) } // Nuevo estado para el interruptor

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
            text = "AGREGAR MEDICAMENTO",
            color = Color(0xFF3498DB),
            fontSize = titleSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1A1A1A))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Seleccionar medicamento",
                    color = Color(0xFFBDC3C7),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WatchButton(
                        text = "Aspirina",
                        width = 80,
                        height = 35,
                        backgroundColor = if (selectedMedication == "Aspirina") listOf(Color(0xFF27AE60), Color(0xFF229954))
                        else listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                        onClick = { selectedMedication = "Aspirina" }
                    )
                    WatchButton(
                        text = "Paracetamol",
                        width = 80,
                        height = 35,
                        backgroundColor = if (selectedMedication == "Paracetamol") listOf(Color(0xFF27AE60), Color(0xFF229954))
                        else listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                        onClick = { selectedMedication = "Paracetamol" }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WatchButton(
                        text = "Ibuprofeno",
                        width = 80,
                        height = 35,
                        backgroundColor = if (selectedMedication == "Ibuprofeno") listOf(Color(0xFF27AE60), Color(0xFF229954))
                        else listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                        onClick = { selectedMedication = "Ibuprofeno" }
                    )
                    WatchButton(
                        text = "Omeprazol",
                        width = 80,
                        height = 35,
                        backgroundColor = if (selectedMedication == "Omeprazol") listOf(Color(0xFF27AE60), Color(0xFF229954))
                        else listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                        onClick = { selectedMedication = "Omeprazol" }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1A1A1A))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Dosis",
                    color = Color(0xFFBDC3C7),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WatchButton(
                        text = "-",
                        width = 40,
                        height = 30,
                        onClick = {
                            val current = dosageAmount.toIntOrNull() ?: 1
                            dosageAmount = (if (current > 1) current - 1 else 1).toString()
                        }
                    )
                    Text(
                        text = dosageAmount,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    WatchButton(
                        text = "+",
                        width = 40,
                        height = 30,
                        onClick = {
                            val current = dosageAmount.toIntOrNull() ?: 1
                            dosageAmount = (current + 1).toString()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WatchButton(
                        text = "pastilla(s)",
                        width = 80,
                        height = 30,
                        backgroundColor = if (dosageUnit == "pastilla(s)") listOf(Color(0xFF27AE60), Color(0xFF229954))
                        else listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                        onClick = { dosageUnit = "pastilla(s)" }
                    )
                    WatchButton(
                        text = "mg",
                        width = 80,
                        height = 30,
                        backgroundColor = if (dosageUnit == "mg") listOf(Color(0xFF27AE60), Color(0xFF229954))
                        else listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                        onClick = { dosageUnit = "mg" }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WatchButton(
                        text = "ml",
                        width = 80,
                        height = 30,
                        backgroundColor = if (dosageUnit == "ml") listOf(Color(0xFF27AE60), Color(0xFF229954))
                        else listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                        onClick = { dosageUnit = "ml" }
                    )
                    WatchButton(
                        text = "gota(s)",
                        width = 80,
                        height = 30,
                        backgroundColor = if (dosageUnit == "gota(s)") listOf(Color(0xFF27AE60), Color(0xFF229954))
                        else listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                        onClick = { dosageUnit = "gota(s)" }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1A1A1A))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Frecuencia",
                    color = Color(0xFFBDC3C7),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WatchButton(
                        text = "10s",
                        width = 50,
                        height = 30,
                        backgroundColor = if (frequency == 0.00278f) listOf(Color(0xFF27AE60), Color(0xFF229954))
                        else listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                        onClick = { frequency = 0.00278f }
                    )
                    WatchButton(
                        text = "8h",
                        width = 50,
                        height = 30,
                        backgroundColor = if (frequency == 8f) listOf(Color(0xFF27AE60), Color(0xFF229954))
                        else listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                        onClick = { frequency = 8f }
                    )
                    WatchButton(
                        text = "12h",
                        width = 50,
                        height = 30,
                        backgroundColor = if (frequency == 12f) listOf(Color(0xFF27AE60), Color(0xFF229954))
                        else listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                        onClick = { frequency = 12f }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WatchButton(
                        text = "24h",
                        width = 80,
                        height = 30,
                        backgroundColor = if (frequency == 24f) listOf(Color(0xFF27AE60), Color(0xFF229954))
                        else listOf(Color(0xFF34495E), Color(0xFF2C3E50)),
                        onClick = { frequency = 24f }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interruptor para activar/desactivar recordatorios
        WatchButton(
            text = if (remindersEnabled) "Desactivar recordatorios" else "Activar recordatorios",
            width = buttonWidth,
            height = 35,
            backgroundColor = listOf(Color(0xFFF39C12), Color(0xFFE67E22)),
            onClick = { remindersEnabled = !remindersEnabled }
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                    if (selectedMedication.isNotEmpty()) {
                        viewModel.addMedication(
                            name = selectedMedication,
                            dosage = "$dosageAmount $dosageUnit",
                            frequencyHours = frequency,
                            remindersEnabled = remindersEnabled
                        )
                        onSaveClick()
                    }
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