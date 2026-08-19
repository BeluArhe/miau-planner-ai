package com.example.temp_miau.logic

import org.junit.Test
import org.junit.Assert.assertEquals

class RecommendationEngineTest {

    private val engine = RecommendationEngine()

    @Test
    fun `regla de seguridad emocional siempre da FACIL sin importar el resto`() {
        val respuestas = RespuestasEntrevista(
            tiempoDisponible = 2,
            nivelEnergia = 0,
            frecuenciaActividad = 2,
            estadoBienestar = 0,
            experienciaCocinando = 2
        )
        // Aunque tiempo/actividad/cocina estén al máximo, energia=0 y bienestar=0 fuerzan FACIL
        assertEquals(Nivel.FACIL, engine.calcularNivel(respuestas))
    }

    @Test
    fun `puntaje bajo sin activar regla de seguridad da FACIL`() {
        val respuestas = RespuestasEntrevista(
            tiempoDisponible = 0,
            nivelEnergia = 1,
            frecuenciaActividad = 0,
            estadoBienestar = 1,
            experienciaCocinando = 0
        )
        // Score = (0*1.5) + (1*2.0) + (0*1.0) + (1*1.5) + (0*1.0) = 3.5 <= 4.5
        assertEquals(Nivel.FACIL, engine.calcularNivel(respuestas))
    }

    @Test
    fun `puntaje medio da MEDIO`() {
        val respuestas = RespuestasEntrevista(
            tiempoDisponible = 1,
            nivelEnergia = 1,
            frecuenciaActividad = 1,
            estadoBienestar = 1,
            experienciaCocinando = 1
        )
        // Score = 1.5 + 2.0 + 1.0 + 1.5 + 1.0 = 7.0 (Rango MEDIO > 4.5 y <= 8.0)
        assertEquals(Nivel.MEDIO, engine.calcularNivel(respuestas))
    }

    @Test
    fun `puntaje maximo da DIFICIL`() {
        val respuestas = RespuestasEntrevista(
            tiempoDisponible = 2,
            nivelEnergia = 2,
            frecuenciaActividad = 2,
            estadoBienestar = 2,
            experienciaCocinando = 2
        )
        // Score = 3.0 + 4.0 + 2.0 + 3.0 + 2.0 = 14.0 (> 8.0)
        assertEquals(Nivel.DIFICIL, engine.calcularNivel(respuestas))
    }

    @Test
    fun `nivelToDificultadString devuelve los strings exactos usados en Room`() {
        assertEquals("facil", engine.nivelToDificultadString(Nivel.FACIL))
        assertEquals("medio", engine.nivelToDificultadString(Nivel.MEDIO))
        assertEquals("dificil", engine.nivelToDificultadString(Nivel.DIFICIL))
    }
}