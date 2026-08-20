package com.example.temp_miau.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.temp_miau.data.AppDatabase
import com.example.temp_miau.data.EntrevistaRegistro
import com.example.temp_miau.data.RecipeAssetLoader
import com.example.temp_miau.logic.*
import com.example.temp_miau.model.Recipe
import com.example.temp_miau.security.CryptoManager
import com.example.temp_miau.sensor.CatMood
import com.example.temp_miau.sensor.StepSensorManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val recipeDao = database.recipeDao()
    private val assetLoader = RecipeAssetLoader(application)
    private val recommendationEngine = RecommendationEngine()
    private val entrevistaDao = database.entrevistaDao()
    private val cryptoManager = CryptoManager()
    val stepSensorManager = StepSensorManager(application)

    private val sharedPrefs = application.getSharedPreferences("miau_secure_profile", Context.MODE_PRIVATE)

    private val _totalRecipes = MutableStateFlow(0)
    val totalRecipes: StateFlow<Int> = _totalRecipes.asStateFlow()

    private val _currentLevel = MutableStateFlow(Nivel.MEDIO)
    val currentLevel: StateFlow<Nivel> = _currentLevel.asStateFlow()

    private val _currentRecipe = MutableStateFlow<Recipe?>(
        Recipe(
            title = "Bowl Energético de Salmón con Quinoa y Espárragos",
            ingredients = listOf("1 filete de salmón fresco", "1 taza de quinoa cocida", "Espárragos trigueros", "Aceite de oliva"),
            instructions = listOf("Dorar el salmón a la plancha", "Saltear los espárragos", "Servir sobre la quinoa tibia"),
            sourceUrl = "https://www.allrecipes.com/recipe/228285/pan-seared-salmon-with-asparagus/",
            dificultad = "medio"
        )
    )
    val currentRecipe: StateFlow<Recipe?> = _currentRecipe.asStateFlow()

    private val _isInterviewOpen = MutableStateFlow(false)
    val isInterviewOpen: StateFlow<Boolean> = _isInterviewOpen.asStateFlow()

    private val _isProfileOpen = MutableStateFlow(false)
    val isProfileOpen: StateFlow<Boolean> = _isProfileOpen.asStateFlow()

    private val _selectedRecipeUrl = MutableStateFlow<String?>(null)
    val selectedRecipeUrl: StateFlow<String?> = _selectedRecipeUrl.asStateFlow()

    private val _selectedAvatar = MutableStateFlow("Naranjito 🐱")
    val selectedAvatar: StateFlow<String> = _selectedAvatar.asStateFlow()

    // Perfil biométrico y cálculos en tiempo real
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _biometria = MutableStateFlow(BiometricsCalculator.calcularBiometriaCompleta(UserProfile()))
    val biometria: StateFlow<BiometriaResultado> = _biometria.asStateFlow()

    val currentSteps: StateFlow<Int> = stepSensorManager.currentSteps
    val catMood: StateFlow<CatMood> = stepSensorManager.catMood

    init {
        cargarPerfilCifrado()
        initializeData()
    }

    fun initializeData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var count = recipeDao.getRecipeCount()
                if (count == 0) {
                    val recipes = assetLoader.loadRecipesFromAssets("recipes.json")
                    if (recipes.isNotEmpty()) {
                        recipes.chunked(500).forEach { batch ->
                            recipeDao.insertRecipes(batch)
                        }
                    }
                    count = recipeDao.getRecipeCount()
                }

                _totalRecipes.value = count
                fetchRandomRecipeForLevel(_currentLevel.value)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun submitInterview(respuestas: RespuestasEntrevista) {
        val nuevoNivel = recommendationEngine.calcularNivel(respuestas)
        _currentLevel.value = nuevoNivel
        _isInterviewOpen.value = false
        fetchRandomRecipeForLevel(nuevoNivel)
        guardarRegistroCifrado(respuestas, nuevoNivel)
    }

    fun saveUserProfile(profile: UserProfile) {
        _userProfile.value = profile
        _biometria.value = BiometricsCalculator.calcularBiometriaCompleta(profile)
        _isProfileOpen.value = false
        guardarPerfilCifrado(profile)
    }

    private fun guardarPerfilCifrado(profile: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val jsonStr = Json.encodeToString(profile)
                val (cifrado, iv) = cryptoManager.encrypt(jsonStr)
                sharedPrefs.edit()
                    .putString("enc_profile", cifrado)
                    .putString("enc_profile_iv", iv)
                    .apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun cargarPerfilCifrado() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cifrado = sharedPrefs.getString("enc_profile", null)
                val iv = sharedPrefs.getString("enc_profile_iv", null)
                if (cifrado != null && iv != null) {
                    val jsonStr = cryptoManager.decrypt(cifrado, iv)
                    val profile = Json.decodeFromString<UserProfile>(jsonStr)
                    _userProfile.value = profile
                    _biometria.value = BiometricsCalculator.calcularBiometriaCompleta(profile)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun guardarRegistroCifrado(respuestas: RespuestasEntrevista, nivel: Nivel) {
        viewModelScope.launch(Dispatchers.IO) {
            val payload = RegistroPayload(
                respuestas = respuestas,
                nivel = recommendationEngine.nivelToDificultadString(nivel)
            )
            val jsonPlano = Json.encodeToString(payload)
            val (cifrado, iv) = cryptoManager.encrypt(jsonPlano)

            entrevistaDao.insertRegistro(
                EntrevistaRegistro(
                    timestampMillis = System.currentTimeMillis(),
                    datosCifrados = cifrado,
                    iv = iv
                )
            )
        }
    }

    fun fetchRandomRecipeForLevel(nivel: Nivel) {
        viewModelScope.launch(Dispatchers.IO) {
            val difStr = recommendationEngine.nivelToDificultadString(nivel)
            val recipe = recipeDao.getRandomRecipeByDificultad(difStr)
            if (recipe != null) {
                _currentRecipe.value = recipe
            }
        }
    }

    fun simulateSteps(additional: Int) {
        stepSensorManager.simulateSteps(additional)
    }

    fun setInterviewOpen(open: Boolean) {
        _isInterviewOpen.value = open
    }

    fun setProfileOpen(open: Boolean) {
        _isProfileOpen.value = open
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

@Serializable
data class RegistroPayload(
    val respuestas: RespuestasEntrevista,
    val nivel: String
)