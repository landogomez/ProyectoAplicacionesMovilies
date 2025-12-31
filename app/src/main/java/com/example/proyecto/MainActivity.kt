package com.example.proyecto


import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavType
import kotlinx.coroutines.delay
import logica.GameState
import androidx.navigation.navArgument


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController() //  controlador de navegación

            NavHost(
                navController = navController,
                startDestination = "loading" // pantalla inicial
            ) {
                composable("loading") { LoadingScreen(navController) }
                composable("menu") { MenuScreen(navController) }
                composable("options") { OptionsScreen(navController) }
                composable(
                    route = "game/{isTwoPlayer}",
                    arguments = listOf(navArgument("isTwoPlayer") { type = NavType.BoolType })
                ) { backStackEntry ->
                    val is2P = backStackEntry.arguments?.getBoolean("isTwoPlayer") ?: false
                    GameScreen(navController, is2P)
                }
            }

        }
    }
}


@Composable
fun MenuScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        MenuBackground()
        MenuButtons(
            onPlayClick = { isMultijugador ->
                // Esto debe coincidir exactamente con "game/{isTwoPlayer}"
                navController.navigate("game/$isMultijugador")
            },
            onOptionsClick = { navController.navigate("options") })
    }
}


@Composable
fun MenuBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF3B30), // rojo arriba
                        Color.Black         // degradado abajo
                    )
                )
            )
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.BottomCenter)
        ) {
            val ovalHeight = size.height * 0.3f
            val ovalSize = androidx.compose.ui.geometry.Size(size.width, ovalHeight * 2.5f)
            val topLeft = Offset(0f, size.height - ovalHeight)

            // óvalo negro (relleno)
            drawOval(
                color = Color.Black,
                topLeft = topLeft,
                size = ovalSize
            )

            // borde blanco
            drawOval(
                color = Color.White,
                topLeft = topLeft,
                size = ovalSize,
                style = Stroke(width = 8f) // grosor del borde
            )
        }
    }
}




@Composable
fun MenuButtons(
    onPlayClick: (isTwoPlayer: Boolean) -> Unit,
        onOptionsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Parte superior ---
        Text(
            text = "2 PLAYER",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable { onPlayClick(true) }
        )

        Text(
            text = "1 PLAYER",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable {
                    onPlayClick(false)
                }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Fila inferior (Options y Exit) ---
        Row(
            horizontalArrangement = Arrangement.spacedBy(40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "OPTIONS",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Yellow,
                modifier = Modifier
                    .clickable { onOptionsClick() }
            )

            val activity = (LocalContext.current as? Activity)

            Text(
                text = "EXIT",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Yellow,
                modifier = Modifier
                    .clickable { activity?.finish() }
            )

        }
    }
}

@Composable
fun LoadingScreen(navController: NavHostController) {
    var progress by remember { mutableStateOf(0f) }

    // Animación de carga
    LaunchedEffect(true) {
        while (progress < 1f) {
            progress += 0.05f
            delay(150)
        }
        navController.navigate("menu") {
            popUpTo("loading") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "LOADING...",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(20.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .width(220.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
            )
        }
    }
}

@Composable
fun OptionsScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 👇 Reutilizas el mismo fondo que en el menú
        MenuBackground()

        // Contenido encima del fondo
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "OPTIONS",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "← Back",
                fontSize = 24.sp,
                color = Color.Gray,
                modifier = Modifier.clickable { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun GameScreen(navController: NavHostController, isTwoPlayer: Boolean) {
    val gameState = remember { GameState() }.apply {
        isTwoPlayerMode = isTwoPlayer
    }

    LaunchedEffect(Unit) {
        while (true) {
            gameState.update()
            delay(16)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        event.changes.forEach { pointerChange ->
                            if (pointerChange.pressed) {
                                val touchX = pointerChange.position.x
                                val touchY = pointerChange.position.y
                                val normY = (touchY / size.height) * 100

                                // LADO IZQUIERDO: Siempre mueve Pala 1
                                if (touchX < size.width / 2) {
                                    gameState.paddle1Y = (normY - gameState.paddle1Height / 2).coerceIn(0f, 100f - gameState.paddle1Height)
                                }
                                // LADO DERECHO: Mueve Pala 2 SOLO si es modo 2 jugadores
                                else if (isTwoPlayer) {
                                    gameState.paddle2Y = (normY - 7.5f).coerceIn(0f, 85f)
                                }
                            }
                        }
                    }
                }
            }
    ) {
        // Marcador (Capa superior)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = gameState.scorePlayer.toString(),
                fontSize = 50.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            // Línea divisoria central estética
            Box(modifier = Modifier.width(2.dp).height(30.dp).background(Color.Gray))
            Text(
                text = gameState.scoreIA.toString(),
                fontSize = 50.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Bola
            drawCircle(Color.White, 15f, Offset(gameState.ballX * w / 100, gameState.ballY * h / 100))

            // Pala Jugador (Roja)
            drawRect(
                Color(0xFFFF3B30),
                Offset(w * 0.02f, gameState.paddle1Y * h / 100),
                androidx.compose.ui.geometry.Size(25f, gameState.paddle1Height * h / 100)
            )

            // Pala IA (Azul)
            drawRect(
                Color.Cyan,
                Offset(w * 0.95f, gameState.paddle2Y * h / 100),
                androidx.compose.ui.geometry.Size(25f, 15f * h / 100)
            )

        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Button(
                onClick = { gameState.togglePause() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.3f))
            ) {
                Text(if (gameState.isPaused) "▶" else "II", color = Color.White)
            }
        }

        // 4. MENÚ DE PAUSA (Solo visible si isPaused es true)
        if (gameState.isPaused && !gameState.isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("PAUSED", fontSize = 40.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { gameState.togglePause() },
                        modifier = Modifier.width(200.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Text("RESUME", color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            navController.navigate("menu") {
                                popUpTo("menu") { inclusive = true }
                            }
                        },
                        modifier = Modifier.width(200.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("QUIT TO MENU", color = Color.White)
                    }
                }
            }
        }

        if (gameState.isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)), // Fondo semi-transparente
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "GAME OVER",
                        fontSize = 50.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "WINNER: ${gameState.winner}",
                        fontSize = 24.sp,
                        color = Color.Yellow,
                        modifier = Modifier.padding(bottom = 30.dp)
                    )

                    // Botón de REINICIAR
                    Button(
                        onClick = { gameState.restartGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                        modifier = Modifier.width(200.dp).padding(8.dp)
                    ) {
                        Text("RESTART", color = Color.Black)
                    }

                    // Botón de MENÚ PRINCIPAL
                    Button(
                        onClick = { navController.navigate("menu") {
                            popUpTo("menu") { inclusive = true }
                        }},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier.width(200.dp).padding(8.dp)
                    ) {
                        Text("MAIN MENU", color = Color.Black)
                    }
                }
            }
        }
    }
}


