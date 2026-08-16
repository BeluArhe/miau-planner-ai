package com.example.temp_miau

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.temp_miau.data.DatasetBuilder // <-- Cambiamos al DatasetBuilder
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Llamamos a la función que descarga el dataset de 100 recetas
        probarDatasetBuilder()

        setContent {
            // Aquí irá la interfaz visual de tu app
        }
    }

    private fun probarDatasetBuilder() {
        val builder = DatasetBuilder(applicationContext)
        lifecycleScope.launch {
            try {
                Log.d("MIAU_DATASET", "Iniciando la descarga del dataset de 100 recetas...")

                // Ejecutamos la construcción con meta de 100 platos
                val totalGuardados = builder.buildDataset(targetGoal = 100)

                Log.d("MIAU_DATASET", "¡Proceso finalizado! Total de recetas guardadas: $totalGuardados")
            } catch (e: Exception) {
                Log.e("MIAU_DATASET", "Error en el dataset: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}