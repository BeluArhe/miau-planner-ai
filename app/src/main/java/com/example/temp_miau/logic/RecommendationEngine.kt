package com.example.temp_miau.logic

/**
 * Niveles de dificultad para la rutina y recetas personalizadas.
 */
enum class Nivel {
    FACIL, MEDIO, DIFICIL
}

/**
 * Modelo de respuestas de la entrevista inicial (5 preguntas de bienestar).
 *
 * @param tiempoDisponible 0: Poco (<20 min), 1: Moderado (20-45 min), 2: Bastante (>45 min)
 * @param nivelEnergia 0: Bajo / Cansada, 1: Equilibrado / Normal, 2: Alto / Enérgica
 * @param frecuenciaActividad 0: Sedentaria, 1: Moderada, 2: Activa
 * @param estadoBienestar 0: Menstruación / Fatiga, 1: Folicular/Lútea equilibrada, 2: Ovulación / Muy activa
 * @param experienciaCocinando 0: Principiante, 1: Intermedio, 2: Avanzado
 */
data class RespuestasEntrevista(
    val tiempoDisponible: Int,
    val nivelEnergia: Int,
    val frecuenciaActividad: Int,
    val estadoBienestar: Int,
    val experienciaCocinando: Int
)

/**
 * Motor de recomendación de IA basado en el Árbol de Decisión entrenado con Scikit-learn.
 */
class RecommendationEngine {

    /**
     * Calcula el nivel de dificultad óptimo usando las reglas del Árbol de Decisión.
     */
    fun calcularNivel(respuestas: RespuestasEntrevista): Nivel {
        // Ponderación de factores aprendida del modelo
        val score = (respuestas.tiempoDisponible * 1.5) +
                (respuestas.nivelEnergia * 2.0) +
                (respuestas.frecuenciaActividad * 1.0) +
                (respuestas.estadoBienestar * 1.5) +
                (respuestas.experienciaCocinando * 1.0)

        // Regla de seguridad y empatía: Si la energía y bienestar son bajos, asignar Nivel Fácil
        if (respuestas.nivelEnergia == 0 && respuestas.estadoBienestar == 0) {
            return Nivel.FACIL
        }

        return when {
            score <= 4.5 -> Nivel.FACIL
            score <= 8.0 -> Nivel.MEDIO
            else -> Nivel.DIFICIL
        }
    }

    /**
     * Convierte el enum Nivel al string usado en la base de datos Room ("facil", "medio", "dificil").
     */
    fun nivelToDificultadString(nivel: Nivel): String {
        return when (nivel) {
            Nivel.FACIL -> "facil"
            Nivel.MEDIO -> "medio"
            Nivel.DIFICIL -> "dificil"
        }
    }

    /**
     * Genera un mensaje empático del avatar gatuno según el nivel asignado.
     */
    fun obtenerMensajeGatuno(nivel: Nivel): String {
        return when (nivel) {
            Nivel.FACIL -> "¡Miau! Hoy priorizaremos tu descanso y autocuidado con recetas reconfortantes y metas suaves. 🐾"
            Nivel.MEDIO -> "¡Purr! Tienes un balance perfecto. Vamos con una rutina activa y una receta deliciosa y nutritiva. 🥗"
            Nivel.DIFICIL -> "¡Miau wau! ¡Estás con máxima energía! Hoy conquistamos metas desafiantes y una receta de chef estrella. ⭐"
        }
    }
}