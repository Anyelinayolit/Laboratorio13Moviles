package com.example.laboratorio13 // Ajusta a tu paquete

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Ejercicio2Screen() {
    // 1. Estado booleano para alternar entre dos colores
    var isBlue by remember { mutableStateOf(true) }

    // 2. Transición suave de color usando animateColorAsState
    val animatedColor by animateColorAsState(
        targetValue = if (isBlue) Color(0xFF2196F3) else Color(0xFF4CAF50), // Azul a Verde
        animationSpec = tween(durationMillis = 600), // Duración de la transición
        label = "ColorAnimation"
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Barra superior simulada para proteger los iconos del teléfono
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(Color(0xFF202124))
        )

        // Contenido del Ejercicio 2
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { isBlue = !isBlue }) {
                Text("Cambiar Color")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Cuadro que cambia de color suavemente
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(animatedColor)
            )
        }
    }
}