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
    // 1. Estado para controlar si el cuadro es visible o no
    var isVisible by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 2. Botón que altera el estado al hacer clic
        Button(onClick = { isVisible = !isVisible }) {
            Text(if (isVisible) "Ocultar Cuadro" else "Mostrar Cuadro")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Componente de animación que reacciona al estado 'isVisible'
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(),  // Efecto de entrada suave
            exit = fadeOut()   // Efecto de salida suave
        ) {
            // Elemento animado
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Red)
            )
        }
    }
}