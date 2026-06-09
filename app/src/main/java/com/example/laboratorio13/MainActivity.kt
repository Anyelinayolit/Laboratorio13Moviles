package com.example.laboratorio13 // Ajusta a tu paquete

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Forzamos el modo EdgeToEdge del sistema para tomar el control total
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        // 2. Quitamos CUALQUIER neblina, sombra o gradiente nativo haciendo la barra 100% transparente
        window.statusBarColor = AndroidColor.TRANSPARENT
        window.navigationBarColor = AndroidColor.TRANSPARENT

        // 3. Forzamos a que los iconos de la batería y la hora se adapten al tema (oscuro/claro) automáticamente
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true // Iconos oscuros si tu fondo es claro

        setContent {
            MaterialTheme {
                var selectedTab by remember { mutableStateOf(0) }

                Scaffold(
                    // statusBarsPadding empuja el contenido debajo de la barra para que no se monte nada
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                label = { Text("Ej 1") },
                                icon = { Text("1️⃣") }
                            )
                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                label = { Text("Ej 2") },
                                icon = { Text("2️⃣") }
                            )
                            NavigationBarItem(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                label = { Text("Ej 3") },
                                icon = { Text("3️⃣") }
                            )
                            NavigationBarItem(
                                selected = selectedTab == 3,
                                onClick = { selectedTab = 3 },
                                label = { Text("Ej 4") },
                                icon = { Text("4️⃣") }
                            )
                            // AGREGADO: Nuevo botón para el prototipo del Videojuego
                            NavigationBarItem(
                                selected = selectedTab == 4,
                                onClick = { selectedTab = 4 },
                                label = { Text("Juego") },
                                icon = { Text("🎮") }
                            )
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedTab) {
                            0 -> Ejercicio1Screen()
                            1 -> Ejercicio2Screen()
                            2 -> Ejercicio3Screen()
                            3 -> Ejercicio4Screen()
                            // AGREGADO: Enlace a la pantalla del Ejercicio Final
                            4 -> VideojuegoPrototipoScreen()
                        }
                    }
                }
            }
        }
    }
}