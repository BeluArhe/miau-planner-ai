package com.example.temp_miau.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.temp_miau.logic.*
import com.example.temp_miau.ui.theme.*

@Composable
fun ProfileDialog(
    initialProfile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    var alturaStr by remember { mutableStateOf(initialProfile.alturaCm.toInt().toString()) }
    var pesoStr by remember { mutableStateOf(initialProfile.pesoKg.toInt().toString()) }
    var edadStr by remember { mutableStateOf(initialProfile.edad.toString()) }
    var genero by remember { mutableStateOf(initialProfile.genero) }
    var actividad by remember { mutableStateOf(initialProfile.nivelActividad) }
    var objetivo by remember { mutableStateOf(initialProfile.objetivoSalud) }

    val altura = alturaStr.toFloatOrNull() ?: 165f
    val peso = pesoStr.toFloatOrNull() ?: 60f
    val edad = edadStr.toIntOrNull() ?: 25

    val previewProfile = UserProfile(
        alturaCm = altura,
        pesoKg = peso,
        edad = edad,
        genero = genero,
        nivelActividad = actividad,
        objetivoSalud = objetivo
    )
    val biometria = remember(altura, peso, edad, genero, actividad, objetivo) {
        BiometricsCalculator.calcularBiometriaCompleta(previewProfile)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚙️ Mi Perfil Físico & Metas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MiauPeachDark
                )
                Text(
                    text = "Calcularemos tu IMC, peso ideal y calorías diarias para orientar tu plan.",
                    fontSize = 12.sp,
                    color = MiauTextSecondary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))

                // Campos Numéricos (Altura, Peso, Edad)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = alturaStr,
                        onValueChange = { if (it.length <= 3) alturaStr = it },
                        label = { Text("Altura (cm)", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = pesoStr,
                        onValueChange = { if (it.length <= 4) pesoStr = it },
                        label = { Text("Peso (kg)", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = edadStr,
                        onValueChange = { if (it.length <= 2) edadStr = it },
                        label = { Text("Edad", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.9f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Selector de Género
                Text(
                    text = "👤 Género biológico (para gasto metabólico):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MiauTextPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Genero.values().forEach { g ->
                        val isSelected = (g == genero)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MiauPeachPrimary else MiauBackgroundLight,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { genero = g }
                        ) {
                            Text(
                                text = g.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MiauTextPrimary,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Selector de Objetivo Principal
                Text(
                    text = "🎯 Tu objetivo principal:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MiauTextPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ObjetivoSalud.values().forEach { obj ->
                        val isSelected = (obj == objetivo)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MiauPeachPrimary.copy(alpha = 0.15f) else MiauBackgroundLight,
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    if (isSelected) MiauPeachPrimary else Color(0xFFE5DDD5)
                                )
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { objetivo = obj }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = obj.emoji, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = obj.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MiauPeachDark else MiauTextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Selector de Nivel de Actividad
                Text(
                    text = "🏃 Nivel de actividad física habitual:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MiauTextPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    NivelActividad.values().forEach { act ->
                        val isSelected = (act == actividad)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MiauLavenderSecondary.copy(alpha = 0.15f) else MiauBackgroundLight,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { actividad = act }
                        ) {
                            Text(
                                text = act.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MiauLavenderSecondary else MiauTextPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tarjeta de Resultados en Tiempo Real
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MiauCardSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📊 Diagnóstico IMC:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MiauTextPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(biometria.categoriaImc.colorHex).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${biometria.imc} - ${biometria.categoriaImc.nombre}",
                                    color = Color(biometria.categoriaImc.colorHex),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "📍 Peso ideal sugerido: ${biometria.pesoMinimoSaludable} kg - ${biometria.pesoMaximoSaludable} kg",
                            fontSize = 11.sp,
                            color = MiauTextSecondary
                        )
                        Text(
                            text = "🔥 Meta calórica: ~${biometria.caloriasMeta} kcal/día",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MiauPeachDark
                        )
                        Text(
                            text = "💧 Meta de agua diaria: ${biometria.aguaLitrosDiarios} L",
                            fontSize = 11.sp,
                            color = MiauTextSecondary
                        )
                    }
                }

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
                        onClick = { onSave(previewProfile) },
                        colors = ButtonDefaults.buttonColors(containerColor = MiauPeachPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Guardar Perfil 🐾", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
