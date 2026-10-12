package com.finanzia.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/*
 * FinanzIA usa solo tema claro y siempre los mismos colores de marca
 * (sin "color dinámico" de Android 12+, que cambiaría la paleta según el fondo
 * de pantalla del teléfono).
 *
 * Aquí le "traducimos" nuestra paleta a Material 3, para que sus componentes
 * (Text, Checkbox, TextField, etc.) usen nuestros colores sin configurarlos uno a uno.
 */
private val FinanziaColorScheme = lightColorScheme(
    primary = FinanziaColors.Azul,
    onPrimary = FinanziaColors.TextoSobreColor,
    secondary = FinanziaColors.Verde,
    onSecondary = FinanziaColors.TextoSobreColor,
    tertiary = FinanziaColors.Violeta,
    onTertiary = FinanziaColors.TextoSobreColor,
    background = FinanziaColors.Fondo,
    onBackground = FinanziaColors.Tinta,
    surface = FinanziaColors.Superficie,
    onSurface = FinanziaColors.Tinta,
    onSurfaceVariant = FinanziaColors.TextoSecundario,
    outline = FinanziaColors.Borde,
    error = FinanziaColors.Coral,
    onError = FinanziaColors.TextoSobreColor
)

@Composable
fun FinanziaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FinanziaColorScheme,
        typography = Typography,
        content = content
    )
}
