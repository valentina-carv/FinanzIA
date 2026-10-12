package com.finanzia.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/*
 * Tipografía de FinanzIA.
 * El diseño usa Plus Jakarta Sans (títulos y montos) e Inter (texto).
 * Mientras no estén los archivos .ttf en res/font/, usamos la fuente del sistema.
 * Cuando se agreguen, solo hay que cambiar estas dos líneas.
 */
private val FuenteTitulos = FontFamily.Default
private val FuenteTexto = FontFamily.Default

/**
 * Estilos de texto. Se usan como `style = FinanziaTypography.Titulo`
 * en vez de escribir `fontSize = 24.sp` en cada pantalla.
 */
object FinanziaTypography {
    /** Montos grandes, como "$ 3.200". */
    val Monto = TextStyle(
        fontFamily = FuenteTitulos,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    )

    /** Logo "FINANZIA". */
    val Logo = TextStyle(
        fontFamily = FuenteTitulos,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 2.sp
    )

    /** Título principal de cada pantalla. */
    val Titulo = TextStyle(
        fontFamily = FuenteTitulos,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp
    )

    /** Títulos de tarjetas y opciones ("Gastos Fijos"). */
    val Subtitulo = TextStyle(
        fontFamily = FuenteTitulos,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 24.sp
    )

    /** Texto normal y descripciones. */
    val Cuerpo = TextStyle(
        fontFamily = FuenteTexto,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )

    /** Texto pequeño (descripción de opciones, mensajes de error). */
    val CuerpoPequeno = TextStyle(
        fontFamily = FuenteTexto,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )

    /** Texto de botones. */
    val Boton = TextStyle(
        fontFamily = FuenteTitulos,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    )

    /** Etiquetas en mayúsculas ("CORREO ELECTRÓNICO") y píldoras de paso. */
    val Etiqueta = TextStyle(
        fontFamily = FuenteTexto,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp
    )

    /** Emojis de las categorías. */
    val Emoji = TextStyle(
        fontSize = 20.sp,
        lineHeight = 24.sp
    )
}

/** Los mismos estilos, entregados a Material 3 para sus componentes. */
val Typography = Typography(
    headlineMedium = FinanziaTypography.Titulo,
    titleMedium = FinanziaTypography.Subtitulo,
    bodyLarge = FinanziaTypography.Cuerpo,
    bodyMedium = FinanziaTypography.CuerpoPequeno,
    labelLarge = FinanziaTypography.Boton,
    labelMedium = FinanziaTypography.Etiqueta
)
