package com.example.temp_miau.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val sourceUrl: String
)