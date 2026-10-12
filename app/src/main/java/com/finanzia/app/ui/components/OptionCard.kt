package com.finanzia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import com.finanzia.app.R
import com.finanzia.app.ui.theme.Acento
import com.finanzia.app.ui.theme.FinanziaColors
import com.finanzia.app.ui.theme.FinanziaShapes
import com.finanzia.app.ui.theme.FinanziaSizes
import com.finanzia.app.ui.theme.FinanziaSpacing
import com.finanzia.app.ui.theme.FinanziaTypography

/**
 * Tarjeta de opción para elegir una categoría o frecuencia (onboarding).
 * Muestra un emoji, un título, una descripción y un círculo de selección.
 *
 * `acento` define el color cuando está seleccionada:
 * `Acento.Ingreso` (verde) o `Acento.Gasto` (coral).
 */
@Composable
fun OptionCard(
    emoji: String,
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    acento: Acento = Acento.Ingreso
) {
    val state = stringResource(if (selected) R.string.seleccionado else R.string.no_seleccionado)
    val borderColor = if (selected) acento.color else FinanziaColors.Borde
    val borderWidth = if (selected) FinanziaSizes.BordeSeleccion else FinanziaSizes.Borde
    val background = if (selected) acento.fondo else FinanziaColors.Superficie

    Column(
        modifier = modifier
            .clip(FinanziaShapes.Opcion)
            .background(background)
            .border(borderWidth, borderColor, FinanziaShapes.Opcion)
            // selectable: se comporta como un botón de opción (radio) para TalkBack.
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .semantics { stateDescription = state }
            .padding(FinanziaSpacing.M)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(FinanziaSizes.CajaEmoji)
                    .background(acento.fondo, FinanziaShapes.CajaEmoji),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, style = FinanziaTypography.Emoji)
            }
            SelectionCircle(selected = selected, acento = acento)
        }
        Text(
            text = title,
            style = FinanziaTypography.Subtitulo,
            color = FinanziaColors.Tinta,
            modifier = Modifier.padding(top = FinanziaSpacing.SM)
        )
        Text(
            text = description,
            style = FinanziaTypography.CuerpoPequeno,
            color = FinanziaColors.TextoSecundario,
            modifier = Modifier.padding(top = FinanziaSpacing.XXS)
        )
    }
}

/** Círculo de la esquina: vacío si no está seleccionada, relleno con ✓ si sí. */
@Composable
private fun SelectionCircle(selected: Boolean, acento: Acento) {
    if (selected) {
        Box(
            modifier = Modifier
                .size(FinanziaSizes.CirculoSeleccion)
                .background(acento.color, FinanziaShapes.Circulo),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = FinanziaColors.TextoSobreColor,
                modifier = Modifier.size(FinanziaSizes.IconoPequeno)
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(FinanziaSizes.CirculoSeleccion)
                .border(FinanziaSizes.BordeSeleccion, FinanziaColors.Borde, FinanziaShapes.Circulo)
        )
    }
}

@Preview(name = "Tarjetas de opción")
@Composable
private fun OptionCardPreview() {
    PreviewFondo {
        Row(horizontalArrangement = Arrangement.spacedBy(FinanziaSpacing.SM)) {
            OptionCard(
                emoji = "🏠",
                title = "Gastos Fijos",
                description = "Renta, servicios, internet, luz, agua",
                selected = true,
                onClick = {},
                acento = Acento.Gasto,
                modifier = Modifier.weight(1f)
            )
            OptionCard(
                emoji = "🍔",
                title = "Comida",
                description = "Supermercado, despensa y restaurantes",
                selected = false,
                onClick = {},
                acento = Acento.Gasto,
                modifier = Modifier.weight(1f)
            )
        }
        OptionCard(
            emoji = "💼",
            title = "Mensual",
            description = "Recibo un pago fijo cada mes",
            selected = true,
            onClick = {}
        )
    }
}
