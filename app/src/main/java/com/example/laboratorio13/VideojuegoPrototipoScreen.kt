package com.example.laboratorio13 // Ajusta a tu paquete

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VideojuegoPrototipoScreen() {
    // Estado que activa el modo de ataque del Boss
    var isAttacking by remember { mutableStateOf(false) }

    // 1. Animación de Color (Pasa de pasivo a modo Furia)
    val colorAnim by animateColorAsState(
        targetValue = if (isAttacking) Color(0xFFFF1744) else Color(0xFF00E5FF),
        animationSpec = tween(durationMillis = 350),
        label = "BossColor"
    )

    // 2. Animación de Tamaño (Se agranda al atacar)
    val sizeAnim by animateDpAsState(
        targetValue = if (isAttacking) 140.dp else 80.dp,
        animationSpec = tween(durationMillis = 350),
        label = "BossSize"
    )

    // 3. Animación de Posición (Embestida horizontal en la pantalla)
    val positionXAnim by animateDpAsState(
        targetValue = if (isAttacking) 180.dp else 15.dp,
        animationSpec = tween(durationMillis = 350),
        label = "BossPosition"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Fondo oscuro estilo videojuego
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "👾 BOSS BATTLE PROTOTYPE 👾",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campo de batalla / Escenario del juego
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .border(2.dp, Color(0xFF333333))
                .background(Color(0xFF1E1E1E))
        ) {
            // El Jefe Componible (Reacciona a las 3 animaciones simultáneamente)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .offset(x = positionXAnim, y = 70.dp) // Aplica la posición calculada
                    .size(sizeAnim)                     // Aplica el tamaño calculado
                    .clip(CircleShape)
                    .background(colorAnim)              // Aplica el color intermedio calculado
            ) {
                // Cambia el emoji según el estado de agresividad
                Text(
                    text = if (isAttacking) "👺" else "🛸",
                    fontSize = (sizeAnim.value / 2.5).sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de acción para desatar el ataque
        Button(
            onClick = { isAttacking = !isAttacking },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isAttacking) Color(0xFFFF1744) else Color(0xFF333333)
            ),
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text(
                text = if (isAttacking) "¡RETROCEDER!" else "¡DESATAR ATAQUE!",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}