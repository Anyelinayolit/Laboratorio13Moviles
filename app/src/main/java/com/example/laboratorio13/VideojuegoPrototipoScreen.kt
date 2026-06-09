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

// Modelos de datos mutables de alto rendimiento
class MeteoritoConVida(
    val id: Int,
    var angle: Float,
    var distance: Float,
    var size: Float,
    var individualSpeed: Float,
    var hp: Int,
    var hitFlashFrames: Int
)

class BalaMutable(
    var angle: Float,
    var distance: Float,
    var active: Boolean
)

class AstroStarMutable(var x: Float, var y: Float, val speed: Float, val size: Float)

// [NUEVO] Modelo para controlar al OVNI jefe
class OvniJefe(
    var isActive: Boolean,
    var angle: Float,
    var distance: Float,
    var hp: Int,
    var hitFlashFrames: Int,
    var directionClockwise: Boolean // Para que haga zig-zag orbitando
)

@Composable
fun VideojuegoPrototipoScreen() {
    var earthLives by remember { mutableStateOf(3) }
    var score by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var isEarthHit by remember { mutableStateOf(false) }
    var isShieldBlock by remember { mutableStateOf(false) }

    var shieldAngle by remember { mutableStateOf(0f) }
    var globalSpeedMultiplier by remember { mutableStateOf(1.2f) }
    var timeElapsedSeconds by remember { mutableStateOf(0) }
    var renderTick by remember { mutableStateOf(0) }

    // Instancia fija del OVNI
    val ovni = remember { OvniJefe(isActive = false, angle = 0f, distance = 270f, hp = 8, hitFlashFrames = 0, directionClockwise = true) }

    val listaMeteoritos = remember {
        mutableStateListOf<MeteoritoConVida>().apply {
            repeat(4) { index ->
                add(MeteoritoConVida(
                    id = index,
                    angle = Random.nextFloat() * 360f,
                    distance = 260f + (index * 75f) + Random.nextInt(0, 40),
                    size = Random.nextInt(26, 40).toFloat(),
                    individualSpeed = Random.nextFloat() * 0.4f + 0.5f,
                    hp = 4,
                    hitFlashFrames = 0
                ))
            }
        }
    }

    val listaBalas = remember { mutableStateListOf<BalaMutable>() }
    val estrellas = remember {
        mutableStateListOf<AstroStarMutable>().apply {
            repeat(30) {
                add(AstroStarMutable(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 0.0015f + 0.0008f, Random.nextFloat() * 3.5f + 1f))
            }
        }
    }

    val shieldRadiusMax = 95f
    val shieldRadiusMin = 75f
    val balaSpeed = 7.0f

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

    // MOTOR DE JUEGO A 60 FPS
    LaunchedEffect(isPlaying) {
        var frameCounter = 0
        var shootCooldown = 0

        while (isPlaying && earthLives > 0) {
            delay(16)
            frameCounter++

            // Control del reloj del juego
            if (frameCounter >= 60) {
                frameCounter = 0
                timeElapsedSeconds++

                // Dificultad incremental gradual de meteoritos
                if (timeElapsedSeconds % 7 == 0) {
                    globalSpeedMultiplier += 0.10f
                }

                // [NUEVO] DETECTOR CLAVE: APARECER OVNI EXACTAMENTE A LOS 6 SEGUNDOS
                if (timeElapsedSeconds >= 6 && !ovni.isActive && ovni.hp > 0) {
                    ovni.isActive = true
                    ovni.distance = 270f // Aparece bien lejos en el espacio exterior
                    ovni.angle = Random.nextFloat() * 360f
                    ovni.hp = 8 // Reinicia su vida de jefe
                }
            }

            // Ráfaga automática de disparos desde el escudo
            if (shootCooldown > 0) shootCooldown--
            if (shootCooldown == 0) {
                listaBalas.add(BalaMutable(angle = shieldAngle, distance = 85f, active = true))
                shootCooldown = 12
            }

            // Fondo estelar animado
            estrellas.forEach { star ->
                star.y += star.speed
                if (star.y > 1f) { star.y = 0f; star.x = Random.nextFloat() }
            }

            // Movimiento de las balas
            for (b in listaBalas) {
                if (b.active) {
                    b.distance += balaSpeed
                    if (b.distance > 280f) b.active = false
                }
            }
            listaBalas.removeAll { !it.active }

            val normalShield = ((shieldAngle % 360f) + 360f) % 360f

            // [NUEVO] MECÁNICAS Y FÍSICAS DEL OVNI (SI YA PASARON LOS 6 SEGUNDOS)
            if (ovni.isActive) {
                if (ovni.hitFlashFrames > 0) ovni.hitFlashFrames--

                // Movimiento inteligente del OVNI: baja lento pero orbita de lado a lado (zig-zag)
                ovni.distance -= 0.4f // Baja muy despacio hacia el planeta
                if (ovni.directionClockwise) ovni.angle += 1.5f else ovni.angle -= 1.5f

                // Cambia de dirección de zig-zag al azar cada cierto tiempo para esquivar balas
                if (Random.nextInt(0, 80) == 10) ovni.directionClockwise = !ovni.directionClockwise

                val normalOvni = ((ovni.angle % 360f) + 360f) % 360f

                // Impacto de Balas contra el OVNI
                for (b in listaBalas) {
                    if (b.active) {
                        val normalBala = ((b.angle % 360f) + 360f) % 360f
                        val diffBalaOvni = kotlin.math.abs(normalBala - normalOvni)
                        val angleMatch = diffBalaOvni <= 28f || diffBalaOvni >= 332f
                        val distMatch = kotlin.math.abs(b.distance - ovni.distance) <= 22f

                        if (angleMatch && distMatch) {
                            b.active = false
                            ovni.hp -= 1
                            ovni.hitFlashFrames = 5 // Destello de daño

                            if (ovni.hp <= 0) {
                                score += 1000 // Gran recompensa por destruir al OVNI Jefe
                                ovni.isActive = false
                            }
                            break
                        }
                    }
                }

                // Si el OVNI choca tu Escudo
                val diffShieldOvni = kotlin.math.abs(normalShield - normalOvni)
                if (ovni.distance in shieldRadiusMin..shieldRadiusMax && (diffShieldOvni <= 42f || diffShieldOvni >= 318f)) {
                    score += 250
                    isShieldBlock = true
                    ovni.isActive = false // Desaparece al chocar escudo
                    delay(20)
                    isShieldBlock = false
                }
                // Si el OVNI burla tu defensa y choca la Tierra
                else if (ovni.distance <= 40f) {
                    earthLives -= 1
                    isEarthHit = true
                    ovni.isActive = false
                    delay(60)
                    isEarthHit = false
                    if (earthLives <= 0) isPlaying = false
                }
            }

            // FÍSICAS DE LOS METEORITOS CONTINUAS
            for (met in listaMeteoritos) {
                met.distance -= (globalSpeedMultiplier * met.individualSpeed)
                if (met.hitFlashFrames > 0) met.hitFlashFrames--

                val normalMeteor = ((met.angle % 360f) + 360f) % 360f

                // Colisión Bala vs Meteorito
                for (b in listaBalas) {
                    if (b.active) {
                        val normalBala = ((b.angle % 360f) + 360f) % 360f
                        val diffBalaMetAngle = kotlin.math.abs(normalBala - normalMeteor)
                        val angleMatch = diffBalaMetAngle <= 25f || diffBalaMetAngle >= 335f
                        val distanceMatch = kotlin.math.abs(b.distance - met.distance) <= 18f

                        if (angleMatch && distanceMatch) {
                            b.active = false
                            met.hp -= 1
                            met.hitFlashFrames = 4

                            if (met.hp <= 0) {
                                score += 400
                                met.angle = Random.nextFloat() * 360f
                                met.distance = 280f + Random.nextInt(40, 140)
                                met.size = Random.nextInt(26, 40).toFloat()
                                met.individualSpeed = Random.nextFloat() * 0.4f + 0.5f
                                met.hp = 4
                            }
                            break
                        }
                    }
                }

                // Intercepción Escudo vs Meteorito
                val diffShield = kotlin.math.abs(normalShield - normalMeteor)
                if (met.distance in shieldRadiusMin..shieldRadiusMax && (diffShield <= 42f || diffShield >= 318f)) {
                    score += 100
                    isShieldBlock = true
                    met.angle = Random.nextFloat() * 360f
                    met.distance = 280f + Random.nextInt(40, 140)
                    met.size = Random.nextInt(26, 40).toFloat()
                    met.individualSpeed = Random.nextFloat() * 0.4f + 0.5f
                    met.hp = 4
                    delay(20)
                    isShieldBlock = false
                }
                // Impacto Tierra vs Meteorito
                else if (met.distance <= 40f) {
                    earthLives -= 1
                    isEarthHit = true
                    met.angle = Random.nextFloat() * 360f
                    met.distance = 280f + Random.nextInt(40, 140)
                    met.size = Random.nextInt(26, 40).toFloat()
                    met.individualSpeed = Random.nextFloat() * 0.4f + 0.5f
                    met.hp = 4
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
        // PANEL SUPERIOR (HUD)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("SCORE: $score", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("TIEMPO: ${timeElapsedSeconds}s " + if (timeElapsedSeconds < 6) "(OVNI en ${6 - timeElapsedSeconds}s)" else "⚠️ ¡OVNI INVASOR!",
                    color = if (timeElapsedSeconds >= 6) Color(0xFFA855F7) else Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp, fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "VIDAS: " + "💙 ".repeat(earthLives).ifEmpty { "💀 EXTINCIÓN" },
                color = if (earthLives == 1) Color(0xFFEF4444) else Color(0xFF22C55E),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // PANTALLA TÁCTICA
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

            // Estrellas
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

            // Dibujar Balas Láser
            if (isPlaying && currentTick >= 0) {
                listaBalas.forEach { bala ->
                    val radB = (bala.angle - 90f) * (PI / 180).toFloat()
                    val bx = bala.distance.dp * cos(radB)
                    val by = bala.distance.dp * sin(radB)
                    Box(modifier = Modifier.offset(x = bx, y = by).size(6.dp).clip(CircleShape).background(Color(0xFF00FFCC)))
                }
            }

            // PLANETA TIERRA (tierra.jpeg)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(115.dp).clip(CircleShape).background(earthAtmosphereColor)
            ) {
                if (earthLives > 0) {
                    Image(
                        painter = painterResource(id = com.example.laboratorio13.R.drawable.tierra),
                        contentDescription = "Planeta Tierra",
                        modifier = Modifier.size(80.dp).clip(CircleShape)
                    )
                } else {
                    Text(text = "💥", fontSize = 54.sp)
                }
            }

            // ESCUDO TORRETA ORBITAL
            Box(
                modifier = Modifier.size(170.dp).rotate(shieldAngle),
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

            // [NUEVO] RENDERING EN TIEMPO REAL DEL OVNI JEFE (Aparece a los 6s)
            if (isPlaying && ovni.isActive && currentTick >= 0) {
                val radO = (ovni.angle - 90f) * (PI / 180).toFloat()
                val ox = ovni.distance.dp * cos(radO)
                val oy = ovni.distance.dp * sin(radO)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.offset(x = ox, y = oy).size(45.dp) // Nave un poco más grande
                ) {
                    Image(
                        painter = painterResource(id = com.example.laboratorio13.R.drawable.ovni), // Vinculado a ovni.jpeg
                        contentDescription = "OVNI Jefe",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(if (ovni.hitFlashFrames > 0) Color.Red.copy(alpha = 0.6f) else Color.Transparent)
                    )
                    Text(
                        text = "👽${ovni.hp}",
                        color = Color.Magenta,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 2.dp)
                            .align(Alignment.TopCenter)
                    )
                }
            }

            // Meteoritos en pantalla
            if (isPlaying && currentTick >= 0) {
                listaMeteoritos.forEach { met ->
                    val rad = (met.angle - 90f) * (PI / 180).toFloat()
                    val currentX = met.distance.dp * cos(rad)
                    val currentY = met.distance.dp * sin(rad)

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.offset(x = currentX, y = currentY).size(met.size.dp)
                    ) {
                        Image(
                            painter = painterResource(id = com.example.laboratorio13.R.drawable.meteorito),
                            contentDescription = "Meteorito",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(if (met.hitFlashFrames > 0) Color.Red.copy(alpha = 0.6f) else Color.Transparent)
                        )
                        Text(
                            text = "❤️${met.hp}",
                            color = Color.Yellow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 2.dp)
                                .align(Alignment.TopCenter)
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
                    listaBalas.clear()
                    ovni.isActive = false
                    ovni.hp = 8

                    listaMeteoritos.forEachIndexed { i, met ->
                        met.angle = Random.nextFloat() * 360f
                        met.distance = 260f + (i * 75f) + Random.nextInt(0, 40)
                        met.size = Random.nextInt(26, 40).toFloat()
                        met.individualSpeed = Random.nextFloat() * 0.4f + 0.5f
                        met.hp = 4
                        met.hitFlashFrames = 0
                    }
                }
                isPlaying = !isPlaying
            },
            colors = ButtonDefaults.buttonColors(containerColor = if (isPlaying) Color(0xFFEF4444) else Color(0xFF22C55E)),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(
                text = if (isPlaying) "⏸️ PAUSAR COMBATE" else if (earthLives == 0) "🔄 RECONSTRUIR REINO" else "🚀 INICIAR DEFENSA ARMADA",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}