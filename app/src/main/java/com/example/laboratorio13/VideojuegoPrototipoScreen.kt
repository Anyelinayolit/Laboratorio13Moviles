package com.example.laboratorio13 // Ajusta a tu paquete

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class MeteoritoMutable(
    val id: Int,
    var angle: Float,
    var distance: Float,
    var size: Float,
    var individualSpeed: Float
)

class AstroStarMutable(var x: Float, var y: Float, val speed: Float, val size: Float)

@Composable
fun VideojuegoPrototipoScreen() {
    var earthLives by remember { mutableStateOf(3) }
    var score by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var isEarthHit by remember { mutableStateOf(false) }
    var isShieldBlock by remember { mutableStateOf(false) }

    var shieldAngle by remember { mutableStateOf(0f) }

    // VELOCIDAD CALIBRADA: Subida de 0.6f a 1.2f para mayor dinamismo inicial
    var globalSpeedMultiplier by remember { mutableStateOf(1.2f) }
    var timeElapsedSeconds by remember { mutableStateOf(0) }
    var renderTick by remember { mutableStateOf(0) }

    val listaMeteoritos = remember {
        mutableStateListOf<MeteoritoMutable>().apply {
            repeat(4) { index ->
                add(MeteoritoMutable(
                    id = index,
                    angle = Random.nextFloat() * 360f,
                    distance = 250f + (index * 75f) + Random.nextInt(0, 40),
                    size = Random.nextInt(22, 38).toFloat(), // Un poquito más grandes para apreciar tu imagen
                    individualSpeed = Random.nextFloat() * 0.4f + 0.5f
                ))
            }
        }
    }

    val estrellas = remember {
        mutableStateListOf<AstroStarMutable>().apply {
            repeat(30) {
                add(AstroStarMutable(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 0.0015f + 0.0008f, Random.nextFloat() * 3.5f + 1f))
            }
        }
    }

    val shieldRadiusMax = 95f
    val shieldRadiusMin = 75f

    val earthAtmosphereColor by animateColorAsState(
        targetValue = if (isEarthHit) Color(0xFFEF4444) else Color(0xFF22D3EE).copy(alpha = 0.2f),
        animationSpec = tween(durationMillis = 60),
        label = "FlashTierra"
    )
    val shieldColor by animateColorAsState(
        targetValue = if (isShieldBlock) Color(0xFFFFEB3B) else Color(0xFF22C55E),
        animationSpec = tween(durationMillis = 60),
        label = "ColorEscudo"
    )

    LaunchedEffect(isPlaying) {
        var frameCounter = 0
        while (isPlaying && earthLives > 0) {
            delay(16)
            frameCounter++

            if (frameCounter >= 60) {
                frameCounter = 0
                timeElapsedSeconds++
                if (timeElapsedSeconds % 7 == 0) {
                    globalSpeedMultiplier += 0.12f
                }
            }

            estrellas.forEach { star ->
                star.y += star.speed
                if (star.y > 1f) { star.y = 0f; star.x = Random.nextFloat() }
            }

            val normalShield = ((shieldAngle % 360f) + 360f) % 360f

            for (met in listaMeteoritos) {
                met.distance -= (globalSpeedMultiplier * met.individualSpeed)

                val normalMeteor = ((met.angle % 360f) + 360f) % 360f
                val diff = kotlin.math.abs(normalShield - normalMeteor)
                val isShieldAligned = diff <= 42f || diff >= 318f

                if (met.distance in shieldRadiusMin..shieldRadiusMax && isShieldAligned) {
                    score += 150
                    isShieldBlock = true

                    met.angle = Random.nextFloat() * 360f
                    met.distance = 280f + Random.nextInt(40, 120)
                    met.size = Random.nextInt(22, 38).toFloat()
                    met.individualSpeed = Random.nextFloat() * 0.4f + 0.5f

                    delay(20)
                    isShieldBlock = false
                }
                else if (met.distance <= 40f) {
                    earthLives -= 1
                    isEarthHit = true

                    met.angle = Random.nextFloat() * 360f
                    met.distance = 280f + Random.nextInt(40, 120)
                    met.size = Random.nextInt(22, 38).toFloat()
                    met.individualSpeed = Random.nextFloat() * 0.4f + 0.5f

                    delay(60)
                    isEarthHit = false

                    if (earthLives <= 0) { isPlaying = false; break }
                }
            }
            renderTick++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("SCORE: $score", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("SOBREVIVIDO: ${timeElapsedSeconds}s", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            }
            Text(
                text = "VIDAS: " + "💙 ".repeat(earthLives).ifEmpty { "💀 EXTINCIÓN" },
                color = if (earthLives == 1) Color(0xFFEF4444) else Color(0xFF22C55E),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                .background(Color(0xFF020617))
                .pointerInput(isPlaying) {
                    if (isPlaying) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            val centerWidth = size.width / 2
                            val centerHeight = size.height / 2
                            val mouseX = change.position.x - centerWidth
                            val mouseY = change.position.y - centerHeight
                            var degree = (atan2(mouseY, mouseX) * (180 / PI).toFloat()) + 90f
                            if (degree < 0) degree += 360f
                            shieldAngle = degree
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            val currentTick = renderTick

            Canvas(modifier = Modifier.fillMaxSize()) {
                estrellas.forEach { star ->
                    drawCircle(
                        color = Color.White.copy(alpha = if (isPlaying) 0.7f else 0.2f),
                        radius = star.size,
                        center = Offset(star.x * size.width, star.y * size.height)
                    )
                }
            }

            Box(modifier = Modifier.size(360.dp).border(1.dp, Color(0xFF1E293B).copy(alpha = 0.3f), CircleShape))
            Box(modifier = Modifier.size(170.dp).border(1.dp, Color(0xFF1E293B).copy(alpha = 0.5f), CircleShape))

            // PLANETA TIERRA (tierra.jpeg)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(115.dp)
                    .clip(CircleShape)
                    .background(earthAtmosphereColor)
            ) {
                if (earthLives > 0) {
                    Image(
                        painter = painterResource(id = com.example.laboratorio13.R.drawable.tierra),
                        contentDescription = "Planeta Tierra",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Text(text = "💥", fontSize = 54.sp)
                }
            }

            // ESCUDO PROTECTOR
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .rotate(shieldAngle),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .background(shieldColor)
                        .border(1.5.dp, Color.White, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )
            }

            // [NUEVO] RENDERIZADO CON IMAGEN PROPIA PARA LOS METEORITOS (Cero lag)
            if (isPlaying && currentTick >= 0) {
                listaMeteoritos.forEach { met ->
                    val rad = (met.angle - 90f) * (PI / 180).toFloat()
                    val currentX = met.distance.dp * cos(rad)
                    val currentY = met.distance.dp * sin(rad)

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .offset(x = currentX, y = currentY)
                            .size(met.size.dp)
                    ) {
                        Image(
                            painter = painterResource(id = com.example.laboratorio13.R.drawable.meteorito), // Vinculado a tu meteorito.jpeg/.png
                            contentDescription = "Meteorito",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }
                }
            }

            if (!isPlaying && earthLives <= 0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💥 COLISIÓN APOCALÍPTICA", color = Color.Red, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("SOBREVIVISTE: ${timeElapsedSeconds}s | TOTAL: $score PTS", color = Color.White, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                if (earthLives <= 0) {
                    earthLives = 3
                    score = 0
                    shieldAngle = 0f
                    timeElapsedSeconds = 0
                    globalSpeedMultiplier = 1.2f

                    listaMeteoritos.forEachIndexed { i, met ->
                        met.angle = Random.nextFloat() * 360f
                        met.distance = 250f + (i * 75f) + Random.nextInt(0, 40)
                        met.size = Random.nextInt(22, 38).toFloat()
                        met.individualSpeed = Random.nextFloat() * 0.4f + 0.5f
                    }
                }
                isPlaying = !isPlaying
            },
            colors = ButtonDefaults.buttonColors(containerColor = if (isPlaying) Color(0xFFEF4444) else Color(0xFF22C55E)),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(
                text = if (isPlaying) "⏸️ PAUSAR SIMULACIÓN" else if (earthLives == 0) "🔄 RECONSTRUIR HÁBITAT" else "🚀 ACTIVAR ESCUDOS SIMULTÁNEOS",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}