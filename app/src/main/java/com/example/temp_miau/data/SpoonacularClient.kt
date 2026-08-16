package com.example.temp_miau.data

import com.example.temp_miau.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class SpoonacularClient {
    private val client = OkHttpClient()
    private val apiKey = "d692e743490e48a4bd5acd5ef0981cec"

    suspend fun fetchAndSaveRecipe(query: String, filePath: String): Recipe? = withContext(Dispatchers.IO) {
        try {
            // Usamos complexSearch con addRecipeInformation=true para obtener ingredientes e instrucciones detalladas
            val url = "https://api.spoonacular.com/recipes/complexSearch?query=$query&apiKey=$apiKey&addRecipeInformation=true&number=1"

            val request = Request.Builder().url(url).build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    android.util.Log.e("SPOONACULAR", "Error en la petición: ${response.code}")
                    return@withContext null
                }

                val body = response.body?.string() ?: return@withContext null
                val json = Json { ignoreUnknownKeys = true }
                val jsonObject = json.parseToJsonElement(body).jsonObject
                val results = jsonObject["results"]?.jsonArray

                if (results.isNullOrEmpty()) {
                    android.util.Log.w("SPOONACULAR", "No se encontraron recetas para: $query")
                    return@withContext null
                }

                // Tomamos la primera receta del resultado
                val firstRecipe = results[0].jsonObject
                val title = firstRecipe["title"]?.jsonPrimitive?.content ?: "Sin título"
                val sourceUrl = firstRecipe["sourceUrl"]?.jsonPrimitive?.content ?: ""

                // Extraemos los ingredientes de forma explícita
                val ingredientsList = mutableListOf<String>()
                firstRecipe["extendedIngredients"]?.jsonArray?.forEach { ingredientElem: JsonElement ->
                    val original = ingredientElem.jsonObject["original"]?.jsonPrimitive?.content
                    if (original != null) ingredientsList.add(original)
                }

                // Extraemos las instrucciones (Spoonacular las agrupa por "analyzedInstructions")
                val instructionsList = mutableListOf<String>()
                val analyzedInstructions = firstRecipe["analyzedInstructions"]?.jsonArray
                if (!analyzedInstructions.isNullOrEmpty()) {
                    val steps = analyzedInstructions[0].jsonObject["steps"]?.jsonArray
                    steps?.forEach { stepElem ->
                        val stepText = stepElem.jsonObject["step"]?.jsonPrimitive?.content
                        if (stepText != null) instructionsList.add(stepText)
                    }
                }

                val recipe = Recipe(
                    title = title,
                    ingredients = ingredientsList,
                    instructions = instructionsList,
                    sourceUrl = sourceUrl
                )

                // Guardamos el resultado en el archivo local de la app
                val jsonContent = Json.encodeToString(Recipe.serializer(), recipe)
                File(filePath).writeText(jsonContent)

                android.util.Log.d("SPOONACULAR", "¡Receta descargada y guardada con éxito: $title!")
                return@withContext recipe
            }
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("SPOONACULAR", "Excepción: ${e.message}")
            return@withContext null
        }
    }
}