package com.example.temp_miau

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.temp_miau.ui.MainViewModel
import com.example.temp_miau.ui.screens.DashboardScreen
import com.example.temp_miau.ui.screens.InitialOnboardingScreen
import com.example.temp_miau.ui.screens.SplashScreen
import com.example.temp_miau.ui.theme.Temp_miauTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val activityRecognitionGranted = permissions[Manifest.permission.ACTIVITY_RECOGNITION] ?: false
        val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false
        } else true

        Log.d("MIAU_PERMISOS", "Reconocimiento de Actividad: $activityRecognitionGranted | Notificaciones: $notificationGranted")
        if (activityRecognitionGranted) {
            viewModel.stepSensorManager.startListening()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setContent {
            Temp_miauTheme {
                var showSplash by remember { mutableStateOf(true) }
                val isInitialSetupCompleted by viewModel.isInitialSetupCompleted.collectAsState()

                Crossfade(
                    targetState = when {
                        showSplash -> AppScreenState.SPLASH
                        !isInitialSetupCompleted -> AppScreenState.ONBOARDING
                        else -> AppScreenState.DASHBOARD
                    },
                    animationSpec = tween(600),
                    label = "AppScreenNavigation"
                ) { screenState ->
                    when (screenState) {
                        AppScreenState.SPLASH -> {
                            SplashScreen(
                                onSplashFinished = { showSplash = false }
                            )
                        }
                        AppScreenState.ONBOARDING -> {
                            InitialOnboardingScreen(
                                onComplete = { profile, respuestas ->
                                    viewModel.completeInitialSetup(profile, respuestas)
                                }
                            )
                        }
                        AppScreenState.DASHBOARD -> {
                            DashboardScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }

        solicitarPermisos()
    }

    override fun onResume() {
        super.onResume()
        if (tienePermisoActividad()) {
            viewModel.stepSensorManager.startListening()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stepSensorManager.stopListening()
    }

    private fun tienePermisoActividad(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private fun solicitarPermisos() {
        val permisos = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                permisos.add(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permisos.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permisos.isNotEmpty()) {
            requestPermissionLauncher.launch(permisos.toTypedArray())
        }
    }
}

enum class AppScreenState {
    SPLASH, ONBOARDING, DASHBOARD
}