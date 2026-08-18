package com.example.temp_miau.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.temp_miau.data.AppDatabase
import com.example.temp_miau.data.RecipeAssetLoader
import com.example.temp_miau.logic.Nivel
import com.example.temp_miau.logic.RecommendationEngine
import com.example.temp_miau.logic.RespuestasEntrevista
import com.example.temp_miau.model.Recipe
import com.example.temp_miau.sensor.CatMood
import com.example.temp_miau.sensor.StepSensorManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val recipeDao = database.recipeDao()
    private val assetLoader = RecipeAssetLoader(application)
    private val recommendationEngine = RecommendationEngine()
    val stepSensorManager = StepSensorManager(application)

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _totalRecipes = MutableStateFlow(0)
    val totalRecipes: StateFlow<Int> = _totalRecipes.asStateFlow()

    private val _currentLevel = MutableStateFlow(Nivel.MEDIO)
    val currentLevel: StateFlow<Nivel> = _currentLevel.asStateFlow()

    private val _currentRecipe = MutableStateFlow<Recipe?>(null)
    val currentRecipe: StateFlow<Recipe?> = _currentRecipe.asStateFlow()

    private val _isInterviewOpen = MutableStateFlow(false)
    val isInterviewOpen: StateFlow<Boolean> = _isInterviewOpen.asStateFlow()

    private val _selectedRecipeUrl = MutableStateFlow<String?>(null)
    val selectedRecipeUrl: StateFlow<String?> = _selectedRecipeUrl.asStateFlow()

    private val _selectedAvatar = MutableStateFlow("Naranjito 🐱")
    val selectedAvatar: StateFlow<String> = _selectedAvatar.asStateFlow()

    // Conectamos con el sensor manager
    val currentSteps: StateFlow<Int> = stepSensorManager.currentSteps
    val catMood: StateFlow<CatMood> = stepSensorManager.catMood

    init {
        initializeData()
    }

    fun initializeData() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val existentes = recipeDao.getAllRecipes()
                if (existentes.isEmpty()) {
                    val recipes = assetLoader.loadRecipesFromAssets("recipes.json")
                    if (recipes.isNotEmpty()) {
                        recipes.chunked(500).forEach { batch ->
                            recipeDao.insertRecipes(batch)
                        }
                    }
                }

                val count = recipeDao.getAllRecipes().size
                _totalRecipes.value = count

                // Seleccionar una receta inicial recomendada
                fetchRandomRecipeForLevel(_currentLevel.value)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitInterview(respuestas: RespuestasEntrevista) {
        val nuevoNivel = recommendationEngine.calcularNivel(respuestas)
        _currentLevel.value = nuevoNivel
        _isInterviewOpen.value = false
        fetchRandomRecipeForLevel(nuevoNivel)
    }

    fun fetchRandomRecipeForLevel(nivel: Nivel) {
        viewModelScope.launch(Dispatchers.IO) {
            val difStr = recommendationEngine.nivelToDificultadString(nivel)
            val recetasNivel = recipeDao.getRecipesByDificultad(difStr)
            if (recetasNivel.isNotEmpty()) {
                _currentRecipe.value = recetasNivel.random()
            }
        }
    }

    fun simulateSteps(additional: Int) {
        stepSensorManager.simulateSteps(additional)
    }

    fun setInterviewOpen(open: Boolean) {
        _isInterviewOpen.value = open
    }

    fun openRecipeUrl(url: String) {
        _selectedRecipeUrl.value = url
    }

    fun closeRecipeUrl() {
        _selectedRecipeUrl.value = null
    }

    fun selectAvatar(avatar: String) {
        _selectedAvatar.value = avatar
    }

    fun getCatGreeting(): String {
        return recommendationEngine.obtenerMensajeGatuno(_currentLevel.value)
    }
}
