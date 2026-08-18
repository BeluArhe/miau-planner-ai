package com.example.temp_miau.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StepSensorManager(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val notificationHelper = WellnessNotificationHelper(context)

    private val _currentSteps = MutableStateFlow(0)
    val currentSteps: StateFlow<Int> = _currentSteps.asStateFlow()

    private val _catMood = MutableStateFlow(CatMood.DURMIENDO)
    val catMood: StateFlow<CatMood> = _catMood.asStateFlow()

    private var initialStepBaseline: Int? = null
    private var lastNotifiedMood: CatMood = CatMood.DURMIENDO

    val dailyGoal = 8000

    /**
     * Inicia la escucha de eventos del sensor de pasos.
     */
    fun startListening() {
        if (sensorManager == null) {
            Log.w("STEP_SENSOR", "SensorManager no está disponible en este dispositivo.")
            return
        }

        val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
            Log.d("STEP_SENSOR", "Sensor TYPE_STEP_COUNTER registrado con éxito.")
        } else if (stepDetectorSensor != null) {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_UI)
            Log.d("STEP_SENSOR", "Sensor TYPE_STEP_DETECTOR registrado como respaldo.")
        } else {
            Log.w("STEP_SENSOR", "Dispositivo sin sensor de pasos físico (usando modo simulación para emulador).")
        }
    }

    /**
     * Detiene la escucha para ahorrar batería cuando la pantalla no está activa.
     */
    fun stopListening() {
        sensorManager?.unregisterListener(this)
        Log.d("STEP_SENSOR", "Sensor de pasos pausado.")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                val totalSinceBoot = event.values[0].toInt()
                if (initialStepBaseline == null) {
                    initialStepBaseline = totalSinceBoot
                }
                val sessionSteps = (totalSinceBoot - (initialStepBaseline ?: totalSinceBoot)).coerceAtLeast(0)
                updateSteps(sessionSteps)
            }
            Sensor.TYPE_STEP_DETECTOR -> {
                if (event.values[0] == 1.0f) {
                    updateSteps(_currentSteps.value + 1)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Manejo de cambios de precisión del sensor
    }

    /**
     * Actualiza el conteo de pasos, calcula el estado del gato y envía notificaciones en hitos clave.
     */
    fun updateSteps(newSteps: Int) {
        _currentSteps.value = newSteps
        val newMood = CatMood.fromSteps(newSteps, dailyGoal)
        _catMood.value = newMood

        // Notificar si hay un cambio ascendente de estado en el avatar
        if (newMood != lastNotifiedMood && newMood.ordinal > lastNotifiedMood.ordinal) {
            notificationHelper.sendStepMilestoneNotification(
                titulo = "${newMood.emoji} ${newMood.titulo}",
                mensaje = newMood.mensaje
            )
            lastNotifiedMood = newMood
        }
    }

    /**
     * Función para simulación en emulador o pruebas de debug.
     */
    fun simulateSteps(additionalSteps: Int) {
        val simulated = _currentSteps.value + additionalSteps
        Log.d("STEP_SENSOR", "Simulando +$additionalSteps pasos. Total: $simulated")
        updateSteps(simulated)
    }
}
