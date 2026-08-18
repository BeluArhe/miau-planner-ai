package com.example.temp_miau.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.temp_miau.logic.RespuestasEntrevista
import com.example.temp_miau.ui.theme.MiauPeachPrimary

@Composable
fun InterviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (RespuestasEntrevista) -> Unit
) {
    var tiempo by remember { mutableIntStateOf(1) }
    var energia by remember { mutableIntStateOf(1) }
    var actividad by remember { mutableIntStateOf(1) }
    var bienestar by remember { mutableIntStateOf(1) }
    var cocina by remember { mutableIntStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🐾 Entrevista de Bienestar IA",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MiauPeachPrimary
                )
                Text(
                    text = "Tu gatito asistente adaptará las recetas y rutinas a tu estado de hoy.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))

                // Pregunta 1: Tiempo
                QuestionSection(
                    title = "⏱️ 1. ¿Cuánto tiempo tienes para cocinar hoy?",
                    options = listOf("Poco (<20 min)", "Medio (20-40 min)", "Mucho (>45 min)"),
                    selectedIndex = tiempo,
                    onSelect = { tiempo = it }
                )

                // Pregunta 2: Energía
                QuestionSection(
                    title = "⚡ 2. ¿Cómo está tu nivel de energía?",
                    options = listOf("Bajo / Cansada", "Normal / Equilibrada", "Alto / Con ganas"),
                    selectedIndex = energia,
                    onSelect = { energia = it }
                )

                // Pregunta 3: Actividad
                QuestionSection(
                    title = "🏃‍♀️ 3. ¿Qué nivel de actividad planeas hoy?",
                    options = listOf("Sedentaria / Relax", "Caminata moderada", "Entrenamiento activo"),
                    selectedIndex = actividad,
                    onSelect = { actividad = it }
                )

                // Pregunta 4: Ciclo / Bienestar
                QuestionSection(
                    title = "🌸 4. Fase del Ciclo o Estado Corporal:",
                    options = listOf("Menstruación / Fatiga", "Folicular / Lútea suave", "Ovulación / Enérgica"),
                    selectedIndex = bienestar,
                    onSelect = { bienestar = it }
                )

                // Pregunta 5: Cocina
                QuestionSection(
                    title = "🍳 5. Nivel de experiencia culinaria:",
                    options = listOf("Principiante / Fácil", "Intermedio", "Chef / Elaborado"),
                    selectedIndex = cocina,
                    onSelect = { cocina = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSubmit(
                                RespuestasEntrevista(
                                    tiempoDisponible = tiempo,
                                    nivelEnergia = energia,
                                    frecuenciaActividad = actividad,
                                    estadoBienestar = bienestar,
                                    experienciaCocinando = cocina
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MiauPeachPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Calcular Rutina 🐾", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionSection(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEachIndexed { index, label ->
                val isSelected = (index == selectedIndex)
                OutlinedButton(
                    onClick = { onSelect(index) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) MiauPeachPrimary.copy(alpha = 0.15f) else Color.Transparent,
                        contentColor = if (isSelected) MiauPeachPrimary else MaterialTheme.colorScheme.onSurface
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isSelected) MiauPeachPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    ),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
