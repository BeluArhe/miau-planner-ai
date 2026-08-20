package com.example.temp_miau.logic

import kotlinx.serialization.Serializable

/**
 * Género biológico para cálculos metabólicos precisos (fórmulas Mifflin-St Jeor / Harris-Benedict).
 */
@Serializable
enum class Genero(val label: String) {
    FEMENINO("Femenino 👩"),
    MASCULINO("Masculino 👨"),
    OTRO("Otro / Neutro ✨")
}

/**
 * Nivel de actividad física habitual del usuario.
 */
@Serializable
enum class NivelActividad(val label: String, val factor: Float) {
    SEDENTARIO("Sedentario (Poco o ningún ejercicio)", 1.2f),
    LIGERO("Ligero (1-3 días / semana)", 1.375f),
    MODERADO("Moderado (3-5 días / semana)", 1.55f),
    ACTIVO("Muy Activo (6-7 días / semana)", 1.725f)
}

/**
 * Objetivo principal de salud y composición corporal.
 */
@Serializable
enum class ObjetivoSalud(val label: String, val ajusteCalorico: Int, val emoji: String) {
    BAJAR_PESO("Bajar de peso / Déficit saludable", -400, "📉"),
    MANTENER_PESO("Mantener peso y tonificar", 0, "⚖️"),
    SUBIR_MASA_MUSCULAR("Ganar masa muscular / Superávit", 350, "📈")
}

/**
 * Categorías clínicas de IMC según la Organización Mundial de la Salud (OMS).
 */
enum class CategoriaImc(
    val nombre: String,
    val colorHex: Long,
    val descripcion: String
) {
    BAJO_PESO("Bajo peso", 0xFF64B5F6, "Tu peso está por debajo de lo recomendado. Priorizaremos nutrientes densos y energía."),
    NORMAL("Peso saludable", 0xFF4CAF50, "¡Excelente balance corporal! Mantén tus hábitos activos y alimentación equilibrada."),
    SOBREPESO("Sobrepeso", 0xFFFFA726, "Pequeño exceso sobre el rango ideal. Enfocaremos recetas saciantes y movimiento constante."),
    OBESIDAD("Obesidad", 0xFFEF5350, "Rango que requiere cuidado especial. Caminatas suaves y déficit nutricional guiado.")
}

/**
 * Perfil biométrico completo del usuario.
 */
@Serializable
data class UserProfile(
    val alturaCm: Float = 165f,
    val pesoKg: Float = 62f,
    val edad: Int = 25,
    val genero: Genero = Genero.FEMENINO,
    val nivelActividad: NivelActividad = NivelActividad.MODERADO,
    val objetivoSalud: ObjetivoSalud = ObjetivoSalud.MANTENER_PESO
)

/**
 * Resultados calculados a partir de los datos biométricos.
 */
data class BiometriaResultado(
    val imc: Float,
    val categoriaImc: CategoriaImc,
    val pesoMinimoSaludable: Float,
    val pesoMaximoSaludable: Float,
    val tmb: Float,
    val tdee: Float,
    val caloriasMeta: Int,
    val aguaLitrosDiarios: Float,
    val consejoGatuno: String
)
