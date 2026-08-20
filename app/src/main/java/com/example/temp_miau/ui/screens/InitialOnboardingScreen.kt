package com.example.temp_miau.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.temp_miau.logic.*
import com.example.temp_miau.ui.theme.*

@Composable
fun InitialOnboardingScreen(
    onComplete: (UserProfile, RespuestasEntrevista) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }

    // Paso 1: Datos Biométricos
    var alturaStr by remember { mutableStateOf("165") }
    var pesoStr by remember { mutableStateOf("60") }
    var edadStr by remember { mutableStateOf("25") }
    var genero by remember { mutableStateOf(Genero.FEMENINO) }

    // Paso 2: Objetivos
    var objetivo by remember { mutableStateOf(ObjetivoSalud.MANTENER_PESO) }
    var actividadHabitual by remember { mutableStateOf(NivelActividad.MODERADO) }

    // Paso 3: Entrevista de Hoy
    var tiempo by remember { mutableIntStateOf(1) }
    var energia by remember { mutableIntStateOf(1) }
    var actividadHoy by remember { mutableIntStateOf(1) }
    var bienestar by remember { mutableIntStateOf(1) }
    var cocina by remember { mutableIntStateOf(1) }

    val altura = alturaStr.toFloatOrNull() ?: 165f
    val peso = pesoStr.toFloatOrNull() ?: 60f
    val edad = edadStr.toIntOrNull() ?: 25

    val currentProfile = UserProfile(
        alturaCm = altura,
        pesoKg = peso,
        edad = edad,
        genero = genero,
        nivelActividad = actividadHabitual,
        objetivoSalud = objetivo
    )

    val biometria = remember(altura, peso, edad, genero, actividadHabitual, objetivo) {
        BiometricsCalculator.calcularBiometriaCompleta(currentProfile)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MiauBackgroundLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp)
        ) {
            // Header con progreso
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🐾 Bienvenida a Miau Planner",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MiauPeachDark
                )

                Surface(
                    color = MiauPeachPrimary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Paso $step de 3",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MiauPeachDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Barra de progreso del cuestionario
            LinearProgressIndicator(
                progress = { step / 3f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MiauPeachPrimary,
                trackColor = Color(0xFFEFE8E1)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contenido según el paso
            Box(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (step) {
                        1 -> {
                            // PASO 1: Biometría y Datos Físicos
                            Text(
                                text = "📏 1. Cuéntanos sobre tu cuerpo",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MiauTextPrimary
                            )
                            Text(
                                text = "Necesitamos estos datos para calcular tu IMC y peso ideal saludable.",
                                fontSize = 12.sp,
                                color = MiauTextSecondary
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = alturaStr,
                                    onValueChange = { if (it.length <= 3) alturaStr = it },
                                    label = { Text("Estatura (cm)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = pesoStr,
                                    onValueChange = { if (it.length <= 4) pesoStr = it },
                                    label = { Text("Peso (kg)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = edadStr,
                                    onValueChange = { if (it.length <= 2) edadStr = it },
                                    label = { Text("Edad") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(0.9f),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                            }

                            Text(
                                text = "Género biológico (para gasto metabólico):",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MiauTextPrimary
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Genero.values().forEach { g ->
                                    val isSelected = (g == genero)
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) MiauPeachPrimary else MiauSurfaceLight,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { genero = g }
                                    ) {
                                        Text(
                                            text = g.label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else MiauTextPrimary,
                                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            // Diagnóstico IMC instantáneo
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MiauSurfaceLight),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "📊 Tu IMC calculado:",
                                            fontSize = 13.sp,
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
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "📍 Rango saludable sugerido: ${biometria.pesoMinimoSaludable} kg - ${biometria.pesoMaximoSaludable} kg",
                                        fontSize = 11.sp,
                                        color = MiauTextSecondary
                                    )
                                }
                            }
                        }

                        2 -> {
                            // PASO 2: Objetivos y Nivel de Actividad
                            Text(
                                text = "🎯 2. ¿Cuál es tu meta principal?",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MiauTextPrimary
                            )
                            Text(
                                text = "Personalizaremos las porciones y calorías según lo que quieras lograr.",
                                fontSize = 12.sp,
                                color = MiauTextSecondary
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                ObjetivoSalud.values().forEach { obj ->
                                    val isSelected = (obj == objetivo)
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) MiauPeachPrimary.copy(alpha = 0.15f) else MiauSurfaceLight,
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
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = obj.emoji, fontSize = 20.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = obj.label,
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) MiauPeachDark else MiauTextPrimary
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "🏃 Nivel de actividad física habitual:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MiauTextPrimary
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                NivelActividad.values().forEach { act ->
                                    val isSelected = (act == actividadHabitual)
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) MiauLavenderSecondary.copy(alpha = 0.15f) else MiauSurfaceLight,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { actividadHabitual = act }
                                    ) {
                                        Text(
                                            text = act.label,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MiauLavenderSecondary else MiauTextPrimary,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }

                            // Resumen de Metas
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MiauSurfaceLight),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "🔥 Meta Calórica Estimada: ~${biometria.caloriasMeta} kcal/día",
                                        fontWeight = FontWeight.Bold,
                                        color = MiauPeachDark,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "💧 Meta de Hidratación: ${biometria.aguaLitrosDiarios} L/día",
                                        fontWeight = FontWeight.Medium,
                                        color = MiauTextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        3 -> {
                            // PASO 3: Estado y Bienestar de Hoy
                            Text(
                                text = "🌸 3. ¿Cómo te sientes hoy?",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MiauTextPrimary
                            )
                            Text(
                                text = "Tu gatito asistente adaptará las primeras recetas y rutinas a tu estado actual.",
                                fontSize = 12.sp,
                                color = MiauTextSecondary
                            )

                            OnboardingQuestion(
                                title = "⏱️ Tiempo disponible para cocinar hoy:",
                                options = listOf("Poco (<20 min)", "Medio (20-40 min)", "Mucho (>45 min)"),
                                selectedIndex = tiempo,
                                onSelect = { tiempo = it }
                            )

                            OnboardingQuestion(
                                title = "⚡ Nivel de energía de hoy:",
                                options = listOf("Bajo / Cansada", "Normal / Equilibrada", "Alto / Con ganas"),
                                selectedIndex = energia,
                                onSelect = { energia = it }
                            )

                            OnboardingQuestion(
                                title = "🏃‍♀️ Actividad planeada para hoy:",
                                options = listOf("Sedentaria / Relax", "Caminata moderada", "Entrenamiento activo"),
                                selectedIndex = actividadHoy,
                                onSelect = { actividadHoy = it }
                            )

                            OnboardingQuestion(
                                title = "🌸 Fase del Ciclo o Estado Corporal:",
                                options = listOf("Menstruación / Fatiga", "Folicular / Lútea suave", "Ovulación / Enérgica"),
                                selectedIndex = bienestar,
                                onSelect = { bienestar = it }
                            )

                            OnboardingQuestion(
                                title = "🍳 Nivel de experiencia en la cocina:",
                                options = listOf("Principiante / Fácil", "Intermedio", "Chef / Elaborado"),
                                selectedIndex = cocina,
                                onSelect = { cocina = it }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones de Navegación del Onboarding
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 1) {
                    OutlinedButton(
                        onClick = { step-- },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("⬅️ Atrás", color = MiauTextPrimary)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (step < 3) {
                    Button(
                        onClick = { step++ },
                        colors = ButtonDefaults.buttonColors(containerColor = MiauPeachPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Siguiente ➡️", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            val finalProfile = UserProfile(
                                alturaCm = altura,
                                pesoKg = peso,
                                edad = edad,
                                genero = genero,
                                nivelActividad = actividadHabitual,
                                objetivoSalud = objetivo
                            )
                            val respuestas = RespuestasEntrevista(
                                tiempoDisponible = tiempo,
                                nivelEnergia = energia,
                                frecuenciaActividad = actividadHoy,
                                estadoBienestar = bienestar,
                                experienciaCocinando = cocina
                            )
                            onComplete(finalProfile, respuestas)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MiauPeachPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("¡Comenzar con mi Gatito! 🐾✨", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingQuestion(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = MiauTextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
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
                        contentColor = if (isSelected) MiauPeachPrimary else MiauTextPrimary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isSelected) MiauPeachPrimary else Color(0xFFE0D7D0)
                        )
                    ),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
