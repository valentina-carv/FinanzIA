package com.finanzia.app.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Espacios (márgenes y separaciones) de FinanzIA.
 * Se usan como `Modifier.padding(FinanziaSpacing.M)` en vez de escribir `16.dp`.
 */
object FinanziaSpacing {
    val XXS = 2.dp
    val XS = 4.dp
    val S = 8.dp
    val SM = 12.dp
    val M = 16.dp
    val L = 24.dp
    val XL = 32.dp
    val XXL = 48.dp
}

/**
 * Tamaños fijos de los componentes (alturas, íconos, bordes).
 * Viven aquí para que ningún componente tenga números sueltos.
 */
object FinanziaSizes {
    val AltoBoton = 56.dp
    val AltoCampo = 64.dp
    val Icono = 24.dp
    val IconoPequeno = 18.dp
    val CajaEmoji = 40.dp
    val CirculoSeleccion = 24.dp
    val Borde = 1.dp
    val BordeSeleccion = 2.dp
    val Sombra = 2.dp
    val IndicadorCarga = 24.dp
    val GrosorIndicador = 2.dp
}
