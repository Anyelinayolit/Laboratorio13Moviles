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
    var isBlue by remember { mutableStateOf(true) }

    val animatedColor by animateColorAsState(
        targetValue = if (isBlue) Color(0xFF2196F3) else Color(0xFF4CAF50),
        animationSpec = tween(durationMillis = 600),
        label = "ColorAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { isBlue = !isBlue }) {
            Text("Cambiar Color")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .background(animatedColor)
        )
    }
}