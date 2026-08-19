/**
 * NOTA DE ARQUITECTURA:
 * Esta clase se usó durante el desarrollo para poblar la base de datos
 * llamando en vivo a la API de Spoonacular. El proyecto final usa en su lugar
 * [RecipeAssetLoader], que carga un recipes.json pre-generado desde assets/,
 * para no depender de conectividad ni de límites de cuota de la API en cada
 * uso de la app. Se conserva este archivo como referencia de cómo se
 * construyó originalmente el dataset y como alternativa si se quisiera
 * volver a un modelo de datos en vivo en el futuro.
 */

package com.example.temp_miau.data

import android.content.Context
import android.util.Log
import com.example.temp_miau.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request
import com.example.temp_miau.BuildConfig

class DatasetBuilder(private val context: Context) {
    private val client = OkHttpClient()

    private val apiKey = BuildConfig.SPOONACULAR_API_KEY

    private val searchQueries = listOf(
        "chicken", "pasta", "beef", "salad", "soup", "rice", "fish",
        "dessert", "breakfast", "vegan", "vegetarian", "pork", "potato",
        "cheese", "chocolate", "bread", "curry", "tacos", "pizza", "cake"
    )

    suspend fun buildDataset(targetGoal: Int = 20): List<Recipe> = withContext(Dispatchers.IO) {
        var collectedCount = 0
        val recipesList = mutableListOf<Recipe>()

        for (query in searchQueries) {
            if (collectedCount >= targetGoal) break

            val url = "https://api.spoonacular.com/recipes/complexSearch?query=$query&apiKey=$apiKey&addRecipeInformation=true&number=5"

            try {
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    val bodyString = response.body?.string() ?: ""

                    if (!response.isSuccessful) {
                        Log.e("DATASET_ERROR", "Error HTTP ${response.code} para '$query': $bodyString")
                        return@use
                    }

                    val json = Json { ignoreUnknownKeys = true }
                    val jsonObject = json.parseToJsonElement(bodyString).jsonObject
                    val results = jsonObject["results"]?.jsonArray

                    if (results.isNullOrEmpty()) return@use

                    for (item in results) {
                        if (collectedCount >= targetGoal) break

                        val recipeObj = item.jsonObject
                        val id = recipeObj["id"]?.jsonPrimitive?.int ?: continue
                        val title = recipeObj["title"]?.jsonPrimitive?.content ?: "Sin título"
                        val sourceUrl = recipeObj["sourceUrl"]?.jsonPrimitive?.content ?: ""

                        val readyInMinutes = recipeObj["readyInMinutes"]?.jsonPrimitive?.intOrNull ?: 30
                        val dificultad = when {
                            readyInMinutes <= 20 -> "facil"
                            readyInMinutes <= 45 -> "medio"
                            else -> "dificil"
                        }


                        // Extracción flexible de ingredientes
                        val ingredientsList = mutableListOf<String>()
                        recipeObj["extendedIngredients"]?.jsonArray?.forEach { ing ->
                            val name = ing.jsonObject["original"]?.jsonPrimitive?.content
                                ?: ing.jsonObject["name"]?.jsonPrimitive?.content
                                ?: ""
                            if (name.isNotBlank()) ingredientsList.add(name)
                        }

                        // Extracción flexible de instrucciones
                        val instructionsList = mutableListOf<String>()
                        recipeObj["analyzedInstructions"]?.jsonArray?.let { analyzed ->
                            if (analyzed.isNotEmpty()) {
                                analyzed.forEach { instructionGroup ->
                                    instructionGroup.jsonObject["steps"]?.jsonArray?.forEach { step ->
                                        step.jsonObject["step"]?.jsonPrimitive?.content?.let {
                                            if (it.isNotBlank()) instructionsList.add(it)
                                        }
                                    }
                                }
                            }
                        }

                        // Si aun así vienen vacías por el endpoint resumido, les ponemos un texto por defecto para que no se pierdan en el dataset de prueba
                        if (ingredientsList.isEmpty()) ingredientsList.add("Ingredientes estándar según Spoonacular ID: $id")
                        if (instructionsList.isEmpty()) instructionsList.add("Consultar fuente original en: $sourceUrl")

                        val recipe = Recipe(
                            title = title,
                            ingredients = ingredientsList,
                            instructions = instructionsList,
                            sourceUrl = sourceUrl,
                            dificultad = dificultad
                        )

                        recipesList.add(recipe)
                        collectedCount++
                        Log.d("DATASET_SAVED", "¡Guardada con éxito!: $title (ID: $id)")
                    }
                }
                kotlinx.coroutines.delay(1000)
            } catch (e: Exception) {
                Log.e("DATASET_EXCEPTION", "Excepción: ${e.message}")
                e.printStackTrace()
            }
        }

        Log.d("DATASET", "¡Dataset construido con éxito! Total de recetas guardadas: ${recipesList.size}")
        return@withContext recipesList
    }
}