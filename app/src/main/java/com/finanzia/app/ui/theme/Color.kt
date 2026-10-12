package com.finanzia.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Paleta "FinanzIA Clean" (ver la tabla de colores en CLAUDE.md y la decisión D-16).
 *
 * Es el ÚNICO lugar del proyecto donde se escriben códigos de color. Las pantallas
 * y componentes usan, por ejemplo, `FinanziaColors.Verde`, nunca `Color(0xFF...)`.
 *
 * Regla de color: verde = dinero que entra, coral = dinero que sale,
 * azul = acciones y enlaces, ámbar = presupuesto cerca del límite,
 * violeta = solo cuando habla la IA.
 */
object FinanziaColors {
    // Base
    val Fondo = Color(0xFFF8F9FD)
    val Superficie = Color(0xFFFFFFFF)
    val Tinta = Color(0xFF0F1B3A)
    val TextoSecundario = Color(0xFF64748B)
    val TextoTenue = Color(0xFF94A3B8)
    val Borde = Color(0xFFE2E8F0)

    // Dinero que entra, éxito y marca
    val Verde = Color(0xFF10B981)
    val VerdeSuave = Color(0xFFECFDF5)

    // Acciones y enlaces
    val Azul = Color(0xFF0062F0)
    val AzulSuave = Color(0xFFEAF2FF)

    // Dinero que sale y errores
    val Coral = Color(0xFFE23D68)
    val CoralSuave = Color(0xFFFFF1F3)

    // Solo la IA
    val Violeta = Color(0xFF7C5CFF)
    val VioletaSuave = Color(0xFFF1EDFF)

    // Presupuesto cerca del límite (≥ 80 %)
    val Ambar = Color(0xFFFFB547)

    // Texto sobre botones de color (verde, azul, coral)
    val TextoSobreColor = Superficie

    /** Degradado horizontal del botón principal: Verde → Azul. */
    val DegradadoPrincipal = Brush.horizontalGradient(listOf(Verde, Azul))

    /** Opacidad de un botón deshabilitado (1 = opaco, 0 = invisible). */
    const val OpacidadDeshabilitado = 0.5f
}

/**
 * Pareja "color fuerte + fondo suave" que comparten varios componentes
 * (OptionCard, StepPill, TipCard). Así el color siempre sigue la regla:
 * Ingreso = verde, Gasto = coral, Info = azul, IA = violeta.
 */
enum class Acento(val color: Color, val fondo: Color) {
    Ingreso(FinanziaColors.Verde, FinanziaColors.VerdeSuave),
    Gasto(FinanziaColors.Coral, FinanziaColors.CoralSuave),
    Info(FinanziaColors.Azul, FinanziaColors.AzulSuave),
    IA(FinanziaColors.Violeta, FinanziaColors.VioletaSuave)
}
