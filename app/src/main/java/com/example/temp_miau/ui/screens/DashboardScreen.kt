package com.example.temp_miau.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.temp_miau.logic.Nivel
import com.example.temp_miau.model.Recipe
import com.example.temp_miau.ui.MainViewModel
import com.example.temp_miau.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val isLoading by viewModel.isLoading.collectAsState()
    val totalRecipes by viewModel.totalRecipes.collectAsState()
    val currentSteps by viewModel.currentSteps.collectAsState()
    val catMood by viewModel.catMood.collectAsState()
    val currentLevel by viewModel.currentLevel.collectAsState()
    val currentRecipe by viewModel.currentRecipe.collectAsState()
    val isInterviewOpen by viewModel.isInterviewOpen.collectAsState()
    val selectedRecipeUrl by viewModel.selectedRecipeUrl.collectAsState()
    val selectedAvatar by viewModel.selectedAvatar.collectAsState()

    val avatars = listOf("Naranjito 🐱", "Siamés 🐾", "Blanquito ❄️", "Panterita 🖤")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🐾 Miau Planner AI", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    Text(
                        text = "$totalRecipes recetas",
                        fontSize = 12.sp,
                        color = MiauPeachDark,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MiauPeachPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "🐾 Tu gatito está cargando 5,000 recetas...",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Selector de Avatar del Gatito
                AvatarSelectorSection(
                    avatars = avatars,
                    selectedAvatar = selectedAvatar,
                    onSelect = { viewModel.selectAvatar(it) }
                )

                // 1. Tarjeta Hero del Avatar Gatuno
                CatHeroCard(
                    catMood = catMood,
                    avatarName = selectedAvatar,
                    onSimulateStep = { viewModel.simulateSteps(500) }
                )

                // 2. Tarjeta del Contador de Pasos
                StepCounterCard(
                    steps = currentSteps,
                    goal = viewModel.stepSensorManager.dailyGoal
                )

                // 3. Tarjeta de Rutina y Bienestar IA
                AIRoutineCard(
                    level = currentLevel,
                    greeting = viewModel.getCatGreeting(),
                    onOpenInterview = { viewModel.setInterviewOpen(true) }
                )

                // 4. Tarjeta de Receta Sugerida del Día
                RecipeOfTheDayCard(
                    recipe = currentRecipe,
                    level = currentLevel,
                    onRefresh = { viewModel.fetchRandomRecipeForLevel(currentLevel) },
                    onOpenUrl = { url -> viewModel.openRecipeUrl(url) }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Diálogo de Entrevista de 5 Preguntas
        if (isInterviewOpen) {
            InterviewDialog(
                onDismiss = { viewModel.setInterviewOpen(false) },
                onSubmit = { respuestas -> viewModel.submitInterview(respuestas) }
            )
        }

        // WebView en pantalla completa para la receta
        selectedRecipeUrl?.let { url ->
            RecipeWebViewScreen(
                url = url,
                onClose = { viewModel.closeRecipeUrl() }
            )
        }
    }
}

@Composable
fun AvatarSelectorSection(
    avatars: List<String>,
    selectedAvatar: String,
    onSelect: (String) -> Unit
) {
    Column {
        Text(
            text = "Elige tu compañero felino:",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(avatars) { avatar ->
                val isSelected = (avatar == selectedAvatar)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MiauPeachPrimary else MaterialTheme.colorScheme.surface,
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isSelected) MiauPeachDark else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    ),
                    modifier = Modifier.clickable { onSelect(avatar) }
                ) {
                    Text(
                        text = avatar,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CatHeroCard(
    catMood: com.example.temp_miau.sensor.CatMood,
    avatarName: String,
    onSimulateStep: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(catMood.colorHex).copy(alpha = 0.2f))
                    .border(2.dp, Color(catMood.colorHex), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = catMood.emoji,
                    fontSize = 42.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "$avatarName - ${catMood.titulo}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = catMood.mensaje,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.padding(vertical = 6.dp)
            )

            Button(
                onClick = onSimulateStep,
                colors = ButtonDefaults.buttonColors(containerColor = MiauPeachPrimary),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("👟 +500 Pasos (Simular en Emulador)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StepCounterCard(steps: Int, goal: Int) {
    val progress = (steps.toFloat() / goal).coerceIn(0f, 1f)
    val km = (steps * 0.00075f)
    val kcal = (steps * 0.04f).toInt()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👟 Actividad Física Diaria",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$steps / $goal pasos",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MiauPeachPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = MiauMintTertiary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = "🔥 $kcal kcal quemadas", fontSize = 12.sp, color = MiauTextSecondary)
                Text(text = "📍 ${"%.2f".format(km)} km recorridos", fontSize = 12.sp, color = MiauTextSecondary)
                Text(text = "🎯 ${(progress * 100).toInt()}% de la meta", fontSize = 12.sp, color = MiauTextSecondary)
            }
        }
    }
}

@Composable
fun AIRoutineCard(
    level: Nivel,
    greeting: String,
    onOpenInterview: () -> Unit
) {
    val (colorBadge, labelBadge) = when (level) {
        Nivel.FACIL -> Pair(EasyGreen, "NIVEL FÁCIL (AUTOCUIDADO)")
        Nivel.MEDIO -> Pair(MediumYellow, "NIVEL MEDIO (BALANCE)")
        Nivel.DIFICIL -> Pair(HardRed, "NIVEL DIFÍCIL (ALTA ENERGÍA)")
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🧠 Rutina Recomendada por IA",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = colorBadge.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = labelBadge,
                        color = colorBadge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = greeting, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onOpenInterview,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("📝 Rehacer Entrevista de Bienestar (5 Preguntas)")
            }
        }
    }
}

@Composable
fun RecipeOfTheDayCard(
    recipe: Recipe?,
    level: Nivel,
    onRefresh: () -> Unit,
    onOpenUrl: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🥗 Receta Sugerida del Día",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Cambiar sugerencia",
                        tint = MiauPeachPrimary
                    )
                }
            }

            if (recipe == null) {
                Text(
                    text = "Cargando sugerencia deliciosa...",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                Text(
                    text = recipe.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MiauPeachDark
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ingredientes principales:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    items(recipe.ingredients.take(4)) { ing ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MiauPeachPrimary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = ing,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { onOpenUrl(recipe.sourceUrl) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MiauLavenderSecondary)
                ) {
                    Text("🌐 Ver Preparación en AllRecipes (WebView)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
