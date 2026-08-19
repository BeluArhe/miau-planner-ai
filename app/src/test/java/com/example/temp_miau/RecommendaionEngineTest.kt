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
        // TODO: aunque tiempo/actividad/cocina estén en su máximo (2),
        // energia=0 y bienestar=0 deben forzar Nivel.FACIL por la regla
        // de seguridad emocional. Escribe el assertEquals aquí.
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
        // TODO: calcula el score a mano con la fórmula real:
        // (tiempo*1.5) + (energia*2.0) + (actividad*1.0) + (bienestar*1.5) + (cocina*1.0)
        // y confirma que cae en el rango de FACIL (<= 4.5)
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
        // TODO: con energia=2 y bienestar=2, no se activa la regla de seguridad
        // (esa regla solo aplica cuando AMBOS son 0). Calcula el score y confirma DIFICIL.
    }

    @Test
    fun `nivelToDificultadString devuelve los strings exactos usados en Room`() {
        assertEquals("facil", engine.nivelToDificultadString(Nivel.FACIL))
        assertEquals("medio", engine.nivelToDificultadString(Nivel.MEDIO))
        assertEquals("dificil", engine.nivelToDificultadString(Nivel.DIFICIL))
    }
}