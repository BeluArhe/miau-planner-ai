package com.example.temp_miau.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EntrevistaDao {

    @Insert
    suspend fun insertRegistro(registro: EntrevistaRegistro)

    @Query("SELECT * FROM entrevistas_historial ORDER BY timestampMillis DESC")
    suspend fun getHistorial(): List<EntrevistaRegistro>

    @Query("DELETE FROM entrevistas_historial")
    suspend fun deleteHistorial()
}