package com.example.laboratorio13 // Ajusta a tu paquete

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ScreenState { CARGANDO, CONTENIDO, ERROR }

@Composable
fun Ejercicio4Screen() {
    var currentState by remember { mutableStateOf(ScreenState.CARGANDO) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text("Selector de Estados Avanzado", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        // Botonera estilizada
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { currentState = ScreenState.CARGANDO },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) { Text("Cargando") }

            Button(
                onClick = { currentState = ScreenState.CONTENIDO },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) { Text("Contenido") }

            Button(
                onClick = { currentState = ScreenState.ERROR },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
            ) { Text("Error") }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // MEJORA: Transición combinada de desvanecimiento + deslizamiento vertical (slide)
        AnimatedContent(
            targetState = currentState,
            transitionSpec = {
                (fadeIn(animationSpec = tween(400)) + slideInVertically(animationSpec = tween(400)) { it / 2 }) togetherWith
                        (fadeOut(animationSpec = tween(400)) + slideOutVertically(animationSpec = tween(400)) { -it / 2 })
            },
            label = "StateTransitionAnimation"
        ) { state ->
            // Contenedor visual mejorado con bordes redondeados y sombra aparente
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF5F5F7))
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (state) {
                    ScreenState.CARGANDO -> {
                        // MEJORA: Componente animado nativo que simula una carga real continua
                        CircularProgressIndicator(
                            color = Color(0xFF6200EE),
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "⏳ Conectando con el servidor remoto...",
                            fontSize = 15.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    ScreenState.CONTENIDO -> {
                        Text(
                            text = "🚀 ¡Datos Sincronizados!",
                            fontSize = 18.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "La lista de elementos se ha actualizado de manera exitosa.",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                    ScreenState.ERROR -> {
                        Text(
                            text = "💥 Error de Conexión",
                            fontSize = 18.sp,
                            color = Color(0xFFC62828),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Código de error: 503. Por favor, intente nuevamente.",
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}