package com.example.laboratorio13 // Ajusta a tu paquete

import androidx.compose.animation.core.animateDpAsState
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
fun Ejercicio3Screen() {
    // Estado para controlar si el cuadro está expandido y movido
    var isExpanded by remember { mutableStateOf(false) }

    // Animación para el tamaño (pasa de 80dp a 150dp)
    val sizeAnim by animateDpAsState(
        targetValue = if (isExpanded) 150.dp else 80.dp,
        animationSpec = tween(500),
        label = "SizeAnimation"
    )

    // Animación para la posición horizontal (pasa de 0dp a 100dp)
    val offsetXAnim by animateDpAsState(
        targetValue = if (isExpanded) 100.dp else 0.dp,
        animationSpec = tween(500),
        label = "PositionAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { isExpanded = !isExpanded }) {
            Text("Mover y Escalar")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Contenedor límite para observar el movimiento
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color(0xFFF0F0F0))
                .padding(16.dp)
        ) {
            // Cuadro animado
            Box(
                modifier = Modifier
                    .offset(x = offsetXAnim, y = 30.dp) // Modificador de posición primero
                    .size(sizeAnim)                     // Modificador de tamaño después
                    .background(Color.Magenta)
            )
        }
    }
}