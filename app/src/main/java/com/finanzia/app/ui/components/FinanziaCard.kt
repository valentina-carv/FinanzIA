package com.finanzia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.finanzia.app.ui.theme.FinanziaColors
import com.finanzia.app.ui.theme.FinanziaShapes
import com.finanzia.app.ui.theme.FinanziaSizes
import com.finanzia.app.ui.theme.FinanziaSpacing
import com.finanzia.app.ui.theme.FinanziaTypography

/**
 * Tarjeta blanca de FinanzIA: esquinas de 24dp, borde suave y una sombra ligera.
 *
 * `content` es una "lambda con receptor" (`ColumnScope.() -> Unit`): lo que pongas
 * dentro se apila en una columna, igual que si escribieras `Column { ... }`.
 */
@Composable
fun FinanziaCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = FinanziaColors.Superficie,
    borderColor: Color = FinanziaColors.Borde,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(FinanziaSizes.Sombra, FinanziaShapes.Tarjeta)
            .background(backgroundColor, FinanziaShapes.Tarjeta)
            .border(FinanziaSizes.Borde, borderColor, FinanziaShapes.Tarjeta)
            .padding(FinanziaSpacing.L),
        content = content
    )
}

@Preview(name = "Tarjeta")
@Composable
private fun FinanziaCardPreview() {
    PreviewFondo {
        FinanziaCard {
            Text(
                text = "MONTO ESTIMADO DEL GASTO (COP)",
                style = FinanziaTypography.Etiqueta,
                color = FinanziaColors.TextoSecundario
            )
            Text(
                text = "$ 3.200",
                style = FinanziaTypography.Monto,
                color = FinanziaColors.Tinta
            )
        }
    }
}
