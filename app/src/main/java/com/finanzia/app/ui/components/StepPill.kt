package com.finanzia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.finanzia.app.ui.theme.Acento
import com.finanzia.app.ui.theme.FinanziaShapes
import com.finanzia.app.ui.theme.FinanziaSpacing
import com.finanzia.app.ui.theme.FinanziaTypography

/**
 * Píldora que indica en qué paso va el usuario, por ejemplo "Paso 2 • Gastos".
 * El color sigue al `acento`: verde en ingresos, coral en gastos.
 */
@Composable
fun StepPill(
    text: String,
    modifier: Modifier = Modifier,
    acento: Acento = Acento.Ingreso
) {
    Text(
        text = text,
        style = FinanziaTypography.Etiqueta,
        color = acento.color,
        modifier = modifier
            .background(acento.fondo, FinanziaShapes.Pildora)
            .padding(horizontal = FinanziaSpacing.M, vertical = FinanziaSpacing.S)
    )
}

@Preview(name = "Indicador de paso")
@Composable
private fun StepPillPreview() {
    PreviewFondo {
        StepPill(text = "Paso 1 • Ingresos")
        StepPill(text = "Paso 2 • Gastos", acento = Acento.Gasto)
    }
}
