package com.example.temp_miau.data

import android.content.Context
import android.util.Log
import com.example.temp_miau.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class RecipeAssetLoader(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun loadRecipesFromAssets(fileName: String = "recipes.json"): List<Recipe> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val recipes = json.decodeFromString<List<Recipe>>(jsonString)
            Log.d("MIAU_ASSET_LOADER", "Lectura exitosa: ${recipes.size} recetas desde assets/$fileName")
            recipes
        } catch (e: Exception) {
            Log.e("MIAU_ASSET_LOADER", "Error al leer recetas desde assets/$fileName", e)
            emptyList()
        }
    }
}
