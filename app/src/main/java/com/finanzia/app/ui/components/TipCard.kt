package com.finanzia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.finanzia.app.ui.theme.Acento
import com.finanzia.app.ui.theme.FinanziaColors
import com.finanzia.app.ui.theme.FinanziaShapes
import com.finanzia.app.ui.theme.FinanziaSpacing
import com.finanzia.app.ui.theme.FinanziaTypography

/**
 * Tarjeta de consejo: un título corto en negrita y color (ej. "Dato de Finanzia:")
 * seguido del texto, sobre un fondo suave.
 *
 * Usa `Acento.Ingreso` (verde) para consejos, `Acento.Gasto` (coral) para el
 * "Dato de Finanzia" en gastos y `Acento.IA` (violeta) solo cuando habla la IA.
 */
@Composable
fun TipCard(
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    acento: Acento = Acento.Ingreso
) {
    val content = buildAnnotatedString {
        withStyle(SpanStyle(color = acento.color, fontWeight = FontWeight.Bold)) {
            append(title)
        }
        append(" ")
        append(text)
    }

    Text(
        text = content,
        style = FinanziaTypography.CuerpoPequeno,
        color = FinanziaColors.Tinta,
        modifier = modifier
            .fillMaxWidth()
            .background(acento.fondo, FinanziaShapes.Tarjeta)
            .padding(FinanziaSpacing.M)
    )
}

@Preview(name = "Tarjeta de consejo")
@Composable
private fun TipCardPreview() {
    PreviewFondo {
        TipCard(
            title = "Dato de Finanzia:",
            text = "Identificar tus gastos fijos es la clave para saber cuánto dinero libre tienes.",
            acento = Acento.Gasto
        )
        TipCard(
            title = "Consejo:",
            text = "Registrar tus ingresos apenas llegan te ayuda a saber cuánto tienes de verdad."
        )
        TipCard(
            title = "FinanzIA dice:",
            text = "Este mes gastaste más en salidas, ¿lo revisamos?",
            acento = Acento.IA
        )
    }
}
