package com.example.temp_miau.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entrevistas_historial")
data class EntrevistaRegistro(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestampMillis: Long,
    val datosCifrados: String,
    val iv: String
)