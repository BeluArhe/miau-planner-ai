package com.example.temp_miau.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val title: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val sourceUrl: String
)