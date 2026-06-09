package com.example.laboratorio13 // Ajusta este paquete al tuyo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
fun Ejercicio1Screen() {
    var isVisible by remember { mutableStateOf(true) }

    // Usamos un Column principal para estructurar la barra simulada y el contenido
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // TRUCO: Creamos una barra superior artificial con el color de fondo para resaltar la batería y hora
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars) // Toma el tamaño exacto de la barra del teléfono
                .background(Color(0xFF202124)) // Gris oscuro estilo Android moderno
        )

        // Contenido original del ejercicio (ahora empujado hacia abajo de forma segura)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { isVisible = !isVisible }) {
                Text(if (isVisible) "Ocultar Cuadro" else "Mostrar Cuadro")
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.Red)
                )
            }
        }
    }
}