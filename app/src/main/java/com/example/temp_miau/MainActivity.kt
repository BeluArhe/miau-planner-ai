package com.example.temp_miau

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.temp_miau.data.AppDatabase
import com.example.temp_miau.data.DatasetBuilder
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        probarDatasetYRoom()

        setContent {
            // La interfaz se implementará en las siguientes fases.
        }
    }

    private fun probarDatasetYRoom() {

        val database = AppDatabase.getDatabase(applicationContext)
        val recipeDao = database.recipeDao()
        val builder = DatasetBuilder(applicationContext)

        lifecycleScope.launch {

            try {

                Log.d(
                    "MIAU_ROOM",
                    "========================================"
                )

                Log.d(
                    "MIAU_ROOM",
                    "Iniciando prueba DatasetBuilder + Room"
                )

                val recetasExistentes = recipeDao.getAllRecipes()
                if (recetasExistentes.isNotEmpty()) {
                    Log.d(
                        "MIAU_ROOM",
                        "Ya hay ${recetasExistentes.size} recetas en Room. No se vuelve a llamar a la API."
                    )
                    return@launch
                }

                // 1. Descargar las recetas desde Spoonacular
                val recipes = builder.buildDataset(targetGoal = 100)

                Log.d(
                    "MIAU_ROOM",
                    "Recetas descargadas: ${recipes.size}"
                )

                // 2. Guardar las recetas en Room
                recipeDao.insertRecipes(recipes)

                Log.d(
                    "MIAU_ROOM",
                    "Recetas insertadas en Room: ${recipes.size}"
                )

                // 3. Recuperar las recetas desde Room
                val storedRecipes = recipeDao.getAllRecipes()

                Log.d(
                    "MIAU_ROOM",
                    "Recetas recuperadas desde Room: ${storedRecipes.size}"
                )

                // 4. Mostrar algunas recetas para comprobar que realmente existen
                storedRecipes.take(5).forEach { recipe ->

                    Log.d(
                        "MIAU_ROOM",
                        "ID=${recipe.id} | ${recipe.title}"
                    )
                }

                Log.d(
                    "MIAU_ROOM",
                    "========================================"
                )

            } catch (e: Exception) {

                Log.e(
                    "MIAU_ROOM",
                    "Error durante la prueba de Room",
                    e
                )
            }
        }
    }
}