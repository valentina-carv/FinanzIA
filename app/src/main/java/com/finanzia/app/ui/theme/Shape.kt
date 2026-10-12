package com.finanzia.app.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * Formas (esquinas redondeadas) de FinanzIA.
 * - Tarjetas grandes y botón principal: 24dp.
 * - Campos de texto y opciones: 16dp.
 * - Indicadores de paso ("Paso 2 • Gastos"): píldora completa.
 */
object FinanziaShapes {
    val Tarjeta = RoundedCornerShape(24.dp)
    val Boton = RoundedCornerShape(24.dp)
    val Campo = RoundedCornerShape(16.dp)
    val Opcion = RoundedCornerShape(16.dp)
    val CajaEmoji = RoundedCornerShape(12.dp)
    val Pildora = CircleShape
    val Circulo = CircleShape
}
