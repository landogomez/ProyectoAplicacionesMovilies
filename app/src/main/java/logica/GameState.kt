package logica

import androidx.compose.runtime.*
import kotlin.random.Random

class GameState {
    // Posiciones y velocidades
    var ballX by mutableStateOf(50f)
    var ballY by mutableStateOf(50f)
    var ballVx by mutableStateOf(0.9f)
    var ballVy by mutableStateOf(0.7f)

    var paddle1Y by mutableStateOf(45f)
    var paddle2Y by mutableStateOf(45f)

    // Tamaños ajustables por Power-ups
    var paddle1Height by mutableStateOf(15f)

    // Lógica de Power-ups
    var powerUpX by mutableStateOf(0f)
    var powerUpY by mutableStateOf(0f)
    var isPowerUpVisible by mutableStateOf(false)
    var powerUpTimer by mutableStateOf(0)

    var scorePlayer by mutableIntStateOf(0)
    var scoreIA by mutableIntStateOf(0)
    var isGameOver by mutableStateOf(false)
    var winner by mutableStateOf("")
    var isTwoPlayerMode by mutableStateOf(false)
    var isPaused by mutableStateOf(false)
    var countdownValue by mutableIntStateOf(3)

    // Contador interno para que pase 1 segundo real (60 frames)
    private var framesCounter = 0

    fun update() {
        if (isGameOver) return
        if (isPaused || isGameOver) return
        if (countdownValue > 0) {
            framesCounter++
            if (framesCounter >= 60) { // Un segundo aprox
                countdownValue--
                framesCounter = 0
            }
            return // <--- IMPORTANTE: No mover la bola si estamos contando
        }


        ballX += ballVx
        ballY += ballVy

        // Rebote en techo y suelo
        if (ballY <= 2f || ballY >= 98f) ballVy *= -1

        // Colisión con Pala Jugador (Izquierda)
        if (ballX <= 5f && ballX > 2f && ballY in paddle1Y..(paddle1Y + paddle1Height)) {
            ballVx = Math.abs(ballVx) * 1.05f
            ballX = 5.1f // Para que no se quede trabada
        }

        // Colisión con Pala IA
        if (ballX >= 95f && ballX < 98f && ballY in paddle2Y..(paddle2Y + 15f)) {
            ballVx = -Math.abs(ballVx) * 1.05f
            ballX = 94.9f
        }

        // IA básica
        if (!isTwoPlayerMode) {
            moveAI()
        }


        managePowerUps()


        if (ballX < 0f) {
            scoreIA++
            if (scoreIA >= 10) {
                isGameOver = true
                winner = "IA"
            } else {
                startCountdown(toPlayer = true)
            }
        } else if (ballX > 100f) {
            scorePlayer++
            if (scorePlayer >= 10) {
                isGameOver = true
                winner = "PLAYER"
            } else {
                startCountdown(toPlayer = true)
            }
        }
    }

    // --- LÓGICA DE IA MEJORADA (Con recuperación al centro) ---
    private fun moveAI() {
        val aiHeight = 15f // Altura de la pala de la IA
        val paddleCenter = paddle2Y + (aiHeight / 2)
        val screenCenter = 50f

        // Variable para decidir a dónde quiere ir la IA
        val targetY: Float
        val speed: Float

        // CASO 1: La pelota viene hacia la IA (Ataque/Defensa)
        if (ballVx > 0) {
            // Intentar seguir la pelota
            targetY = ballY
            // Velocidad rápida para alcanzar la bola
            speed = 1.5f
        }
        // CASO 2: La pelota se aleja hacia el jugador (Recuperación)
        else {
            // Volver al centro para estar listo
            targetY = screenCenter
            // Velocidad más relajada para volver a posición
            speed = 0.60f
        }

        // Lógica de movimiento suave (Interpolación)
        val diff = targetY - paddleCenter

        // "Zone Muerta": Si está cerca del objetivo (a menos de 1.5 unidades), no se mueve para evitar temblores
        if (Math.abs(diff) > 1.5f) {
            if (diff > 0) {
                paddle2Y += speed
            } else {
                paddle2Y -= speed
            }
        }

        // Mantener dentro de la pantalla
        paddle2Y = paddle2Y.coerceIn(0f, 100f - aiHeight)
    }



    private fun managePowerUps() {
        // Aparecer un power-up aleatoriamente si no hay uno
        if (!isPowerUpVisible && Random.nextInt(1000) < 5) {
            powerUpX = Random.nextFloat() * 60f + 20f
            powerUpY = Random.nextFloat() * 80f + 10f
            isPowerUpVisible = true
        }

        // Colisión bola con Power-up
        if (isPowerUpVisible && Math.abs(ballX - powerUpX) < 4f && Math.abs(ballY - powerUpY) < 4f) {
            isPowerUpVisible = false
            paddle1Height = 30f // Power-up: Pala gigante
            powerUpTimer = 300   // Duración (aprox 5 segundos a 60fps)
        }

        // Desactivar power-up cuando pase el tiempo
        if (powerUpTimer > 0) {
            powerUpTimer--
            if (powerUpTimer <= 0) paddle1Height = 15f
        }
    }

    // Función auxiliar: Solo coloca la bola y define dirección, pero NO mueve nada aún
    private fun setupBall(toPlayer: Boolean) {
        ballX = 50f
        ballY = 50f

        // Si va al jugador (true) => Velocidad negativa en X
        ballVx = if (toPlayer) -0.9f else 0.9f

        // Y aleatoria en Y
        ballVy = if (kotlin.random.Random.nextBoolean()) 0.7f else -0.7f

        // Reset de Power-Ups (limpieza)
        paddle1Height = 15f
        powerUpTimer = 0
        isPowerUpVisible = false
    }

    fun restartGame() {
        scorePlayer = 0
        scoreIA = 0
        isGameOver = false
        winner = ""
        setupBall(toPlayer = true)
    }
    fun togglePause() {
        isPaused = !isPaused
    }

    fun startCountdown(toPlayer: Boolean) {
        // 1. Llamamos a la función que resetea la posición (la que arreglamos antes)
        setupBall(toPlayer)

        // 2. Iniciamos el conteo en 3
        countdownValue = 3

        // 3. Reseteamos el contador de frames
        framesCounter = 0
    }
}