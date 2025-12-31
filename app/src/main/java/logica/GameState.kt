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

    fun update() {
        if (isGameOver) return
        if (isPaused || isGameOver) return


        ballX += ballVx
        ballY += ballVy

        // 1. Rebote en techo y suelo
        if (ballY <= 2f || ballY >= 98f) ballVy *= -1

        // 2. Colisión con Pala Jugador (Izquierda)
        if (ballX <= 5f && ballX > 2f && ballY in paddle1Y..(paddle1Y + paddle1Height)) {
            ballVx = Math.abs(ballVx) * 1.05f
            ballX = 5.1f // Para que no se quede trabada
        }

        // 3. Colisión con Pala IA (Derecha)
        if (ballX >= 95f && ballX < 98f && ballY in paddle2Y..(paddle2Y + 15f)) {
            ballVx = -Math.abs(ballVx) * 1.05f
            ballX = 94.9f
        }

        // 4. IA básica
        if (!isTwoPlayerMode) {
            if (ballY > paddle2Y + 7.5f) paddle2Y += 0.6f
            else paddle2Y -= 0.6f
            paddle2Y = paddle2Y.coerceIn(0f, 85f)
        }

        // 5. Gestión de Power-ups (la función que ya tenías)
        managePowerUps()

        // 6. ANOTACIÓN (Aquí es donde se llama a resetBall)
        if (ballX < 0f) {
            scoreIA++
            if (scoreIA >= 10) {
                isGameOver = true
                winner = "IA"
            } else {
                resetBall(toPlayer = true)
            }
        } else if (ballX > 100f) {
            scorePlayer++
            if (scorePlayer >= 10) {
                isGameOver = true
                winner = "PLAYER"
            } else {
                resetBall(toPlayer = false)
            }
        }
    }

    // --- AQUÍ ESTABA EL ERROR: Agregamos el parámetro (toPlayer: Boolean) ---
    private fun resetBall(toPlayer: Boolean) {
        ballX = 50f
        ballY = 50f
        // Si toPlayer es true, la bola va hacia el jugador (Vx negativa)
        ballVx = if (toPlayer) -0.9f else 0.9f
        ballVy = if (Random.nextBoolean()) 0.7f else -0.7f

        // Reset opcional de power-ups al anotar
        paddle1Height = 15f
        powerUpTimer = 0
    }

    // Asegúrate de tener la función managePowerUps() abajo...

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

    private fun resetBall() {
        ballX = 50f
        ballY = 50f
        // Invertimos la dirección para que saque el que recibió el punto
        ballVx = if (ballVx > 0) -0.9f else 0.9f
        ballVy = if (Random.nextBoolean()) 0.7f else -0.7f

        // Reset de power-ups al anotar (opcional, para equilibrar)
        paddle1Height = 15f
        powerUpTimer = 0
    }

    fun restartGame() {
        scorePlayer = 0
        scoreIA = 0
        isGameOver = false
        winner = ""
        resetBall(toPlayer = true)
    }
    fun togglePause() {
        isPaused = !isPaused
    }
}