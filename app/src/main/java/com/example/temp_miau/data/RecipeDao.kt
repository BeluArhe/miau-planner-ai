package com.example.temp_miau.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.temp_miau.model.Recipe

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<Recipe>)

    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipes(): List<Recipe>

    @Query("SELECT * FROM recipes WHERE dificultad = :nivel")
    suspend fun getRecipesByDificultad(nivel: String): List<Recipe>

    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun getRecipeCount(): Int

    @Query("SELECT * FROM recipes WHERE dificultad = :nivel ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomRecipeByDificultad(nivel: String): Recipe?

    @Query("DELETE FROM recipes")
    suspend fun deleteAllRecipes()
}