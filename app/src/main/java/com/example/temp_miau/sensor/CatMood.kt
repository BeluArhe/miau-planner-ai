package com.example.temp_miau.sensor

/**
 * Estados de ánimo y reacciones dinámicas del avatar del gato según los pasos alcanzados.
 */
enum class CatMood(
    val titulo: String,
    val mensaje: String,
    val emoji: String,
    val colorHex: Long
) {
    DURMIENDO(
        titulo = "Gatito Dormilón",
        mensaje = "Zzz... Tu gatito duerme en su mantita. ¡Un paseíto corto nos vendría genial para despertar! 🐾",
        emoji = "💤🐱",
        colorHex = 0xFF9E9E9E
    ),
    DESPERTANDO(
        titulo = "Gatito Estirándose",
        mensaje = "¡Miau! El gatito bosteza, se despereza y empieza a seguirte el ritmo. ¡Buen comienzo! 🌤️",
        emoji = "😺✨",
        colorHex = 0xFFFFB74D
    ),
    ACTIVO(
        titulo = "Gatito Aventurero",
        mensaje = "¡Purr purr! ¡Qué buen ritmo llevas hoy! Tu gatito camina alegre a tu lado con la cola en alto. 🧶",
        emoji = "😻🐾",
        colorHex = 0xFF4CAF50
    ),
    CELEBRANDO(
        titulo = "¡Gatito Campeón!",
        mensaje = "¡MIAU WAU! 🎉 ¡Has conquistado la meta del día! Tu gatito hace piruetas y disfruta su pescadito. 🐟⭐",
        emoji = "😸🏆",
        colorHex = 0xFFFFD700
    );

    companion object {
        /**
         * Determina el estado del gato en función de los pasos actuales y la meta diaria.
         */
        fun fromSteps(pasos: Int, metaDiaria: Int = 8000): CatMood {
            val porcentaje = if (metaDiaria > 0) (pasos.toFloat() / metaDiaria) else 0f
            return when {
                porcentaje < 0.25f -> DURMIENDO      // < 2,000 pasos si meta es 8,000
                porcentaje < 0.60f -> DESPERTANDO     // 2,000 - 4,799 pasos
                porcentaje < 1.0f  -> ACTIVO          // 4,800 - 7,999 pasos
                else               -> CELEBRANDO      // 8,000+ pasos
            }
        }
    }
}
