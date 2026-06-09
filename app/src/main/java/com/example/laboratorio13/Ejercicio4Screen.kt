package com.example.laboratorio13 // Ajusta a tu paquete

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. Definición de los tres estados requeridos
enum class ScreenState { CARGANDO, CONTENIDO, ERROR }

@Composable
fun Ejercicio4Screen() {
    // Estado inicial que arranca en CARGANDO
    var currentState by remember { mutableStateOf(ScreenState.CARGANDO) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text("Selector de Estados", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Botonera para alternar de forma interactiva entre estados
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { currentState = ScreenState.CARGANDO }) { Text("Cargando") }
            Button(onClick = { currentState = ScreenState.CONTENIDO }) { Text("Contenido") }
            Button(
                onClick = { currentState = ScreenState.ERROR },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text("Error", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 2. Componente AnimatedContent para manejar las transiciones de estados
        AnimatedContent(
            targetState = currentState,
            transitionSpec = {
                // 3. Efectos personalizados de entrada y salida con tiempos de 500ms
                fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
            },
            label = "StateTransitionAnimation"
        ) { state ->
            // Contenedor visual diferente para cada mensaje en pantalla
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color(0xFFF5F5F5))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    ScreenState.CARGANDO -> {
                        Text("⏳ Cargando datos desde el servidor...", fontSize = 16.sp, color = Color.Gray)
                    }
                    ScreenState.CONTENIDO -> {
                        Text("✅ ¡Éxito! El contenido se cargó correctamente.", fontSize = 16.sp, color = Color(0xFF388E3C), fontWeight = FontWeight.Medium)
                    }
                    ScreenState.ERROR -> {
                        Text("❌ Error: No se pudo establecer conexión.", fontSize = 16.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}