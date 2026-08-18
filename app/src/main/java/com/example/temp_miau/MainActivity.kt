package com.example.temp_miau

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.temp_miau.data.AppDatabase
import com.example.temp_miau.data.RecipeAssetLoader
import com.example.temp_miau.data.RecipeDao
import com.example.temp_miau.logic.RecommendationEngine
import com.example.temp_miau.logic.RespuestasEntrevista
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inicializarDatosYProbar()

        setContent {
            // UI en Compose se conectará en las siguientes fases
        }
    }

    private fun inicializarDatosYProbar() {
        val database = AppDatabase.getDatabase(applicationContext)
        val recipeDao = database.recipeDao()
        val assetLoader = RecipeAssetLoader(applicationContext)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d("MIAU_APP", "========================================")
                Log.d("MIAU_APP", "😺 MIAU PLANNER AI - INICIANDO CARGA DE DATOS")

                val totalExistentes = recipeDao.getAllRecipes().size
                if (totalExistentes == 0) {
                    Log.d("MIAU_APP", "Base de datos Room vacía. Leyendo 5,000 recetas desde assets/recipes.json...")
                    val recipes = assetLoader.loadRecipesFromAssets("recipes.json")
                    
                    if (recipes.isNotEmpty()) {
                        Log.d("MIAU_APP", "Insertando ${recipes.size} recetas en Room en lotes de 500...")
                        // Inserción en bloques (chunks) para máximo rendimiento en SQLite
                        recipes.chunked(500).forEachIndexed { index, batch ->
                            recipeDao.insertRecipes(batch)
                            Log.d("MIAU_APP", "  -> Lote ${index + 1}/${(recipes.size + 499) / 500} insertado (${batch.size} recetas)")
                        }
                        Log.d("MIAU_APP", "✔ ¡Total de ${recipes.size} recetas insertadas con éxito en Room!")
                    } else {
                        Log.w("MIAU_APP", "Advertencia: No se encontraron recetas en assets.")
                    }
                } else {
                    Log.d("MIAU_APP", "Room ya contiene $totalExistentes recetas persistidas.")
                }

                // Probar el motor de IA con respuestas de prueba
                probarRecommendationEngine(recipeDao)

                Log.d("MIAU_APP", "========================================")
            } catch (e: Exception) {
                Log.e("MIAU_APP", "Error durante la inicialización de datos", e)
            }
        }
    }

    private suspend fun probarRecommendationEngine(recipeDao: RecipeDao) {
        Log.d("MIAU_ENGINE", "--- Prueba del Motor de IA (Árbol de Decisión) ---")

        // Ejemplo: Usuaria con energía media y tiempo moderado
        val respuestasTest = RespuestasEntrevista(
            tiempoDisponible = 1,    // Moderado
            nivelEnergia = 1,        // Equilibrado
            frecuenciaActividad = 1, // Moderada
            estadoBienestar = 1,     // Folicular/Lútea
            experienciaCocinando = 0 // Principiante
        )

        val engine = RecommendationEngine()
        val nivel = engine.calcularNivel(respuestasTest)
        val difStr = engine.nivelToDificultadString(nivel)
        val mensajeGatuno = engine.obtenerMensajeGatuno(nivel)

        Log.d("MIAU_ENGINE", "Nivel predicho: $nivel ('$difStr')")
        Log.d("MIAU_ENGINE", "Mensaje del Avatar: $mensajeGatuno")

        val recetasSugeridas = recipeDao.getRecipesByDificultad(difStr)
        Log.d("MIAU_ENGINE", "Recetas sugeridas encontradas en Room para '$difStr': ${recetasSugeridas.size}")
        recetasSugeridas.take(5).forEach { r ->
            Log.d("MIAU_ENGINE", "  -> [${r.dificultad.uppercase()}] ${r.title} (${r.ingredients.size} ingredientes)")
        }
    }
}