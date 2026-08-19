package com.example.temp_miau.ui.screens

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.temp_miau.R
import com.example.temp_miau.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val context = LocalContext.current

    // Configuramos ImageLoader con decodificador GIF compatible con todas las versiones de Android
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        // Animación suave de la barra de progreso
        for (i in 1..100) {
            delay(25)
            progress = i / 100f
        }
        delay(300)
        onSplashFinished()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MiauBackgroundLight
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Tarjeta estilizada que reproduce el GIF animado
                Surface(
                    modifier = Modifier
                        .size(240.dp)
                        .shadow(10.dp, RoundedCornerShape(32.dp)),
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(R.drawable.cat_loading)
                            .crossfade(true)
                            .build(),
                        imageLoader = imageLoader,
                        contentDescription = "Gatito lamiéndose la pata",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "🐾 Miau Planner AI",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MiauPeachDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Afilando las garras y preparando tus recetas... 🧶✨",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MiauTextSecondary
                )

                Spacer(modifier = Modifier.height(28.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .width(180.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MiauPeachPrimary,
                    trackColor = Color(0xFFEFE8E1)
                )
            }
        }
    }
}
