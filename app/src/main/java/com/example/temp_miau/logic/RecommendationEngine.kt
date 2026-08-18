package com.example.temp_miau.logic

enum class Nivel {
    FACIL, MEDIO, DIFICIL
}

data class RespuestasEntrevista(
    val tiempoDisponible: Int,   // 0, 1 o 2
    val nivelEnergia: Int,       // 0, 1 o 2
    val frecuenciaActividad: Int,// 0, 1 o 2
    val estadoBienestar: Int,    // 0, 1 o 2
    val experienciaCocinando: Int// 0, 1 o 2
)

class RecommendationEngine {

    fun calcularNivel(respuestas: RespuestasEntrevista): Nivel {
        val puntajeTotal = respuestas.tiempoDisponible +
                respuestas.nivelEnergia +
                respuestas.frecuenciaActividad +
                respuestas.estadoBienestar +
                respuestas.experienciaCocinando

        return when {
            puntajeTotal <= 3 -> Nivel.FACIL
            puntajeTotal <= 7 -> Nivel.MEDIO
            else -> Nivel.DIFICIL
        }
    }

    fun nivelToDificultadString(nivel: Nivel): String {
        return when (nivel) {
            Nivel.FACIL -> "facil"
            Nivel.MEDIO -> "medio"
            Nivel.DIFICIL -> "dificil"
        }
    }
}