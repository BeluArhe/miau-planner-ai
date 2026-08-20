package com.example.temp_miau

import com.example.temp_miau.logic.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BiometricsCalculatorTest {

    @Test
    fun `calcularImc calcula correctamente el indice de masa corporal`() {
        // Peso: 70kg, Altura: 175cm -> 70 / (1.75 * 1.75) = 22.86 -> 22.9
        val imc = BiometricsCalculator.calcularImc(70f, 175f)
        assertEquals(22.9f, imc, 0.1f)
    }

    @Test
    fun `obtenerCategoriaImc clasifica adecuadamente segun OMS`() {
        assertEquals(CategoriaImc.BAJO_PESO, BiometricsCalculator.obtenerCategoriaImc(17.5f))
        assertEquals(CategoriaImc.NORMAL, BiometricsCalculator.obtenerCategoriaImc(22.0f))
        assertEquals(CategoriaImc.SOBREPESO, BiometricsCalculator.obtenerCategoriaImc(27.3f))
        assertEquals(CategoriaImc.OBESIDAD, BiometricsCalculator.obtenerCategoriaImc(32.1f))
    }

    @Test
    fun `calcularRangoPesoSaludable devuelve rango razonable`() {
        val (min, max) = BiometricsCalculator.calcularRangoPesoSaludable(160f)
        // 18.5 * 1.6^2 = 47.36 -> ~47.4 kg
        // 24.9 * 1.6^2 = 63.74 -> ~63.7 kg
        assertTrue(min in 46f..49f)
        assertTrue(max in 62f..65f)
    }

    @Test
    fun `calcularTmb diferencia entre masculino y femenino`() {
        val tmbFem = BiometricsCalculator.calcularTmb(60f, 165f, 25, Genero.FEMENINO)
        val tmbMasc = BiometricsCalculator.calcularTmb(60f, 165f, 25, Genero.MASCULINO)

        // Masculino debe ser mayor que femenino debido a la fórmula Mifflin-St Jeor (+5 vs -161)
        assertTrue(tmbMasc > tmbFem)
        assertEquals(166f, tmbMasc - tmbFem, 0.5f)
    }

    @Test
    fun `calcularCaloriasMeta aplica deficit o superavit segun objetivo`() {
        val tdee = 2000f
        val calBajar = BiometricsCalculator.calcularCaloriasMeta(tdee, ObjetivoSalud.BAJAR_PESO)
        val calMantener = BiometricsCalculator.calcularCaloriasMeta(tdee, ObjetivoSalud.MANTENER_PESO)
        val calSubir = BiometricsCalculator.calcularCaloriasMeta(tdee, ObjetivoSalud.SUBIR_MASA_MUSCULAR)

        assertEquals(1600, calBajar)
        assertEquals(2000, calMantener)
        assertEquals(2350, calSubir)
    }

    @Test
    fun `calcularAguaLitros calcula aproximadamente 35ml por kilo`() {
        val agua = BiometricsCalculator.calcularAguaLitros(60f)
        // 60 * 35 = 2100 ml = 2.1 L
        assertEquals(2.1f, agua, 0.1f)
    }
}
