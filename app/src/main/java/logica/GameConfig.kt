package logica

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

object GameConfig {
    // Lista de power-ups activos
    var powerUpsEnabled = mutableStateListOf<String>()

    // Estilo visual elegido
    var selectedStyle = mutableStateOf("Retro")
}