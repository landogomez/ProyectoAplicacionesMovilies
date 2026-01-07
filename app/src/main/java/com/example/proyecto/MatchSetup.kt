package com.example.proyecto

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import logica.GameConfig// Asegúrate de que este import sea correcto

@Composable
fun MatchSetup(navController: NavHostController, isTwoPlayer: Boolean) {
    // Variables temporales para guardar la elección del usuario
    var tempStyle by remember { mutableStateOf("Retro") }
    val availablePowerUps = listOf("Big Paddle", "Fast Ball")
    val tempPowerUps = remember { mutableStateListOf<String>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CONFIGURACIÓN",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(30.dp))

            // --- 1. SELECCIONAR ESTILO ---
            Text("ESTILO VISUAL:", color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Retro", "Neon", "Classic").forEach { style ->
                    Button(
                        onClick = { tempStyle = style },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (tempStyle == style) Color.Yellow else Color.DarkGray
                        )
                    ) {
                        Text(style, color = if (tempStyle == style) Color.Black else Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // --- 2. SELECCIONAR POWER-UPS ---
            Text("POWER-UPS:", color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))

            availablePowerUps.forEach { powerUp ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .clickable {
                            if (tempPowerUps.contains(powerUp)) tempPowerUps.remove(powerUp)
                            else tempPowerUps.add(powerUp)
                        }
                        .padding(8.dp)
                ) {
                    Checkbox(
                        checked = tempPowerUps.contains(powerUp),
                        onCheckedChange = null, // Lo manejamos con el Row clickable
                        colors = CheckboxDefaults.colors(checkedColor = Color.Green)
                    )
                    Text(powerUp, color = Color.White, modifier = Modifier.padding(start = 10.dp))
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            // --- 3. BOTÓN JUGAR ---
            Button(
                onClick = {
                    // AQUÍ GUARDAMOS TODO EN LA CONFIGURACIÓN GLOBAL
                    GameConfig.selectedStyle.value = tempStyle
                    GameConfig.powerUpsEnabled.clear()
                    GameConfig.powerUpsEnabled.addAll(tempPowerUps)

                    // Y nos vamos al juego
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