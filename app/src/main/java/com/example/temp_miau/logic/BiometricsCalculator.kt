package com.example.temp_miau.logic

import kotlin.math.roundToInt

/**
 * Motor de cálculos biométricos y metabólicos basados en fórmulas científicas estándar (OMS, Mifflin-St Jeor).
 */
object BiometricsCalculator {

    /**
     * Calcula el Índice de Masa Corporal (IMC): peso (kg) / (altura (m))^2
     */
    fun calcularImc(pesoKg: Float, alturaCm: Float): Float {
        if (alturaCm <= 0f || pesoKg <= 0f) return 0f
        val alturaMetros = alturaCm / 100f
        val imc = pesoKg / (alturaMetros * alturaMetros)
        return (imc * 10).roundToInt() / 10f
    }

    /**
     * Clasifica el IMC según los umbrales de la Organización Mundial de la Salud (OMS).
     */
    fun obtenerCategoriaImc(imc: Float): CategoriaImc {
        return when {
            imc < 18.5f -> CategoriaImc.BAJO_PESO
            imc < 25.0f -> CategoriaImc.NORMAL
            imc < 30.0f -> CategoriaImc.SOBREPESO
            else -> CategoriaImc.OBESIDAD
        }
    }

    /**
     * Calcula el rango de peso saludable (IMC 18.5 a 24.9) para una estatura dada.
     */
    fun calcularRangoPesoSaludable(alturaCm: Float): Pair<Float, Float> {
        if (alturaCm <= 0f) return Pair(0f, 0f)
        val alturaMetros = alturaCm / 100f
        val alturaSq = alturaMetros * alturaMetros
        val minPeso = ((18.5f * alturaSq) * 10).roundToInt() / 10f
        val maxPeso = ((24.9f * alturaSq) * 10).roundToInt() / 10f
        return Pair(minPeso, maxPeso)
    }

    /**
     * Calcula la Tasa Metabólica Basal (TMB / BMR) usando la fórmula de Mifflin-St Jeor.
     */
    fun calcularTmb(pesoKg: Float, alturaCm: Float, edad: Int, genero: Genero): Float {
        val base = (10f * pesoKg) + (6.25f * alturaCm) - (5f * edad)
        return when (genero) {
            Genero.FEMENINO -> base - 161f
            Genero.MASCULINO -> base + 5f
            Genero.OTRO -> base - 78f // Punto medio representativo
        }
    }

    /**
     * Calcula el Gasto Energético Total Diario (TDEE = TMB * factor de actividad).
     */
    fun calcularTdee(tmb: Float, nivelActividad: NivelActividad): Float {
        return tmb * nivelActividad.factor
    }

    /**
     * Calcula las calorías objetivo diarias ajustadas al objetivo de salud.
     */
    fun calcularCaloriasMeta(tdee: Float, objetivoSalud: ObjetivoSalud): Int {
        val calorias = tdee + objetivoSalud.ajusteCalorico
        return calorias.coerceAtLeast(1200f).roundToInt()
    }

    /**
     * Calcula la ingesta de agua recomendada en litros diarios (~35 ml por kg de peso corporal).
     */
    fun calcularAguaLitros(pesoKg: Float): Float {
        val litros = (pesoKg * 35f) / 1000f
        return (litros * 10).roundToInt() / 10f
    }

    /**
     * Genera un consejo empático felino adaptado a la condición física y la meta del usuario.
     */
    fun obtenerConsejoGatuno(categoriaImc: CategoriaImc, objetivo: ObjetivoSalud): String {
        return when (objetivo) {
            ObjetivoSalud.BAJAR_PESO -> when (categoriaImc) {
                CategoriaImc.NORMAL, CategoriaImc.SOBREPESO, CategoriaImc.OBESIDAD ->
                    "¡Miau! Para tu déficit saludable, aumentaremos el volumen con vegetales frescos y caminatas diarias a paso constante. 🐾🥗"
                CategoriaImc.BAJO_PESO ->
                    "¡Purr! Tu peso ya es bajo. Te sugiero enfocarte en nutrirte bien y tonificar suavemente en lugar de recortar calorías. 🐱✨"
            }
            ObjetivoSalud.MANTENER_PESO ->
                "¡Miau perfecto! Mantendremos tu equilibrio actual combinando recetas coloridas y tus 8,000 pasos diarios. 🧶🍲"
            ObjetivoSalud.SUBIR_MASA_MUSCULAR ->
                "¡Miau con fuerza! 💪 Incluiremos más porciones de proteína deliciosa y carbohidratos complejos para que tus músculos crezcan sanos. 🐟🍳"
        }
    }

    /**
     * Ejecuta el cálculo biométrico integral para un perfil de usuario.
     */
    fun calcularBiometriaCompleta(profile: UserProfile): BiometriaResultado {
        val imc = calcularImc(profile.pesoKg, profile.alturaCm)
        val categoria = obtenerCategoriaImc(imc)
        val (minPeso, maxPeso) = calcularRangoPesoSaludable(profile.alturaCm)
        val tmb = calcularTmb(profile.pesoKg, profile.alturaCm, profile.edad, profile.genero)
        val tdee = calcularTdee(tmb, profile.nivelActividad)
        val caloriasMeta = calcularCaloriasMeta(tdee, profile.objetivoSalud)
        val aguaLitros = calcularAguaLitros(profile.pesoKg)
        val consejo = obtenerConsejoGatuno(categoria, profile.objetivoSalud)

        return BiometriaResultado(
            imc = imc,
            categoriaImc = categoria,
            pesoMinimoSaludable = minPeso,
            pesoMaximoSaludable = maxPeso,
            tmb = (tmb * 10).roundToInt() / 10f,
            tdee = (tdee * 10).roundToInt() / 10f,
            caloriasMeta = caloriasMeta,
            aguaLitrosDiarios = aguaLitros,
            consejoGatuno = consejo
        )
    }
}
