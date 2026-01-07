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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role.Companion.Checkbox
import androidx.navigation.NavType
import kotlinx.coroutines.delay
import logica.GameState
import androidx.navigation.navArgument
import logica.GameConfig
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.ui.text.font.FontFamily


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
                    route = "match_setup/{isTwoPlayer}",
                    arguments = listOf(navArgument("isTwoPlayer") { type = NavType.BoolType })
                ) { backStackEntry ->
                    val is2P = backStackEntry.arguments?.getBoolean("isTwoPlayer") ?: false
                    MatchSetup(navController, is2P)
                }
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
                // --- CAMBIO AQUÍ ---
                // En vez de ir a "game", vamos a "match_setup"
                navController.navigate("match_setup/$isMultijugador")
            },
            onOptionsClick = {
                navController.navigate("options")
            }
        )
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
                        Color(0xFFFF3B30),
                        Color.Black
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

            // óvalo negro
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
    var counter by remember { mutableStateOf(0) }
    // 1. Inicializamos estado y configuración
    val gameState = remember { GameState() }.apply {
        isTwoPlayerMode = isTwoPlayer
    }

    // 2. Definimos los colores según el estilo elegido en el PreGame
    val theme = when (GameConfig.selectedStyle.value) {
        "Neon" -> GameTheme(
            bg = Color(0xFF050014),
            ball = Color(0xFF00FFCC),
            p1 = Color(0xFFFF00CC),
            p2 = Color(0xFF00FFCC),
            text = Color(0xFF00FFCC)
        )
        "Classic" -> GameTheme(
            bg = Color(0xFF2B463C),
            ball = Color(0xFFFFD700),
            p1 = Color.White,
            p2 = Color.White,
            text = Color.White
        )
        else -> GameTheme( // Retro
            bg = Color.Black,
            ball = Color.White,
            p1 = Color(0xFFFF3B30),
            p2 = Color(0xFF34C759),
            text = Color.White
        )
    }
    // 3. Bucle del juego
    LaunchedEffect(Unit) {
        while (true) {
            gameState.update()
            kotlinx.coroutines.delay(16)
        }
    }

    // 4. Interfaz Visual
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg) // <--- USA EL FONDO DEL TEMA
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        event.changes.forEach { pointerChange ->
                            if (pointerChange.pressed) {
                                val touchX = pointerChange.position.x
                                val touchY = pointerChange.position.y
                                val normY = (touchY / size.height) * 100

                                // LADO IZQUIERDO: Mueve Pala 1
                                if (touchX < size.width / 2) {
                                    gameState.paddle1Y = (normY - gameState.paddle1Height / 2)
                                        .coerceIn(0f, 100f - gameState.paddle1Height)
                                }
                                // LADO DERECHO: Mueve Pala 2 (Solo si es 2 Players)
                                else if (isTwoPlayer) {
                                    gameState.paddle2Y = (normY - 7.5f).coerceIn(0f, 85f)
                                }
                            }
                        }
                    }
                }
            }
    ) {
        // --- CAPA 1: EL JUEGO (CANVAS) ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // A. Dibujar Red (Línea punteada) - Solo si no es Neon para variar
            if (GameConfig.selectedStyle.value != "Neon") {
                drawLine(
                    color = theme.ball.copy(alpha = 0.3f),
                    start = Offset(w / 2, 0f),
                    end = Offset(w / 2, h),
                    strokeWidth = 4f,
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(20f, 20f))
                )
            }

            // B. Bola
            drawCircle(
                color = theme.ball, // <--- COLOR DEL TEMA
                radius = 15f,
                center = Offset(gameState.ballX * w / 100, gameState.ballY * h / 100)
            )

            // C. Pala Jugador 1
            drawRect(
                color = theme.p1, // <--- COLOR DEL TEMA
                topLeft = Offset(w * 0.02f, gameState.paddle1Y * h / 100),
                size = androidx.compose.ui.geometry.Size(25f, gameState.paddle1Height * h / 100)
            )

            // D. Pala Jugador 2
            drawRect(
                color = theme.p2, // <--- COLOR DEL TEMA
                topLeft = Offset(w * 0.95f, gameState.paddle2Y * h / 100),
                size = androidx.compose.ui.geometry.Size(25f, 15f * h / 100)
            )

            // E. Power-up (Si está visible)
            if (gameState.isPowerUpVisible) {
                drawRect(
                    color = Color.Yellow, // Siempre amarillo para resaltar
                    topLeft = Offset(gameState.powerUpX * w / 100, gameState.powerUpY * h / 100),
                    size = androidx.compose.ui.geometry.Size(40f, 40f)
                )
            }
        }

        // --- CAPA 2: MARCADOR ---
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
                color = theme.text, // <--- Texto con color del tema
                fontWeight = FontWeight.Bold
            )

            // Divisor pequeño
            Box(modifier = Modifier.width(2.dp).height(30.dp).background(theme.text.copy(alpha = 0.5f)))

            Text(
                text = gameState.scoreIA.toString(),
                fontSize = 50.sp,
                color = theme.text, // <--- Texto con color del tema
                fontWeight = FontWeight.Bold
            )
        }

        if (gameState.countdownValue > 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)), // Oscurece un poco el fondo para resaltar el número
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = gameState.countdownValue.toString(),
                    color = theme.text, // Usa el color del tema (Neon, Blanco, etc.)
                    fontSize = 150.sp,  // Tamaño gigante
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    style = androidx.compose.ui.text.TextStyle(
                        // Un efecto de sombra/brillo para que se vea genial
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = theme.ball,
                            blurRadius = 30f
                        )
                    )
                )
            }
        }

        // --- CAPA 3: BOTÓN DE PAUSA ---
        // (Aquí iría el código del botón en la esquina que hicimos antes)
    }
}
@Composable
fun PreGameScreen(navController: NavHostController, isTwoPlayer: Boolean) {
    // Estados temporales para esta pantalla
    var selectedStyle by remember { mutableStateOf("Retro") }

    // Lista de PowerUps disponibles
    val availablePowerUps = listOf("Big Paddle", "Fast Ball", "Slow Motion")
    // Lista de los seleccionados (inicialmente vacía)
    val selectedPowerUps = remember { mutableStateListOf<String>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MATCH SETUP",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(30.dp))

            // --- SECCIÓN 1: ESTILO VISUAL ---
            Text("VISUAL STYLE", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Retro", "Neon", "Classic").forEach { style ->
                    StyleButton(
                        text = style,
                        isSelected = selectedStyle == style,
                        onClick = { selectedStyle = style }
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // --- SECCIÓN 2: POWER-UPS ---
            Text("ACTIVE POWER-UPS", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                availablePowerUps.forEach { powerUp ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .clickable {
                                if (selectedPowerUps.contains(powerUp)) {
                                    selectedPowerUps.remove(powerUp)
                                } else {
                                    selectedPowerUps.add(powerUp)
                                }
                            }
                            .padding(8.dp)
                    ) {
                        Checkbox(
                            checked = selectedPowerUps.contains(powerUp),
                            onCheckedChange = null, // Lo manejamos en el Row click
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color.Green,
                                uncheckedColor = Color.White,
                                checkmarkColor = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = powerUp, color = Color.White, fontSize = 18.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            // --- BOTÓN START ---
            Button(
                onClick = {
                    // 1. Guardamos la configuración en el objeto global
                    GameConfig.selectedStyle.value = selectedStyle
                    GameConfig.powerUpsEnabled.clear()
                    GameConfig.powerUpsEnabled.addAll(selectedPowerUps)

                    // 2. Navegamos al juego
                    navController.navigate("game/$isTwoPlayer")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30))
            ) {
                Text("START GAME", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Un componente auxiliar para los botones de estilo
@Composable
fun StyleButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(100.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color.White else Color.DarkGray)
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.Black else Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

data class GameTheme(
    val bg: Color,
    val ball: Color,
    val p1: Color,
    val p2: Color,
    val text: Color
)


