package com.finanzia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.finanzia.app.R
import com.finanzia.app.ui.theme.FinanziaColors
import com.finanzia.app.ui.theme.FinanziaShapes
import com.finanzia.app.ui.theme.FinanziaSizes
import com.finanzia.app.ui.theme.FinanziaSpacing
import com.finanzia.app.ui.theme.FinanziaTypography

/**
 * Botón principal de FinanzIA.
 *
 * - Por defecto tiene el degradado Verde → Azul (pantallas de login, registro, ingreso).
 * - Si se pasa `solidColor`, el fondo es de un solo color (ej. Azul en la pantalla de gasto).
 * - Con `loading = true` muestra un círculo de progreso y no se puede tocar
 *   (así evitamos el doble toque, F-03 CA-8).
 */
@Composable
fun FinanziaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    solidColor: Color? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    showArrow: Boolean = true
) {
    val canClick = enabled && !loading
    val loadingDescription = stringResource(R.string.cargando)

    // Fondo: un color sólido si nos lo pasan; si no, el degradado de marca.
    val backgroundModifier = if (solidColor != null) {
        Modifier.background(solidColor)
    } else {
        Modifier.background(FinanziaColors.DegradadoPrincipal)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(FinanziaSizes.AltoBoton)
            .alpha(if (enabled) 1f else FinanziaColors.OpacidadDeshabilitado)
            .shadow(FinanziaSizes.Sombra, FinanziaShapes.Boton)
            .clip(FinanziaShapes.Boton)
            .then(backgroundModifier)
            .clickable(enabled = canClick, role = Role.Button, onClick = onClick)
            .padding(horizontal = FinanziaSpacing.L),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(FinanziaSizes.IndicadorCarga)
                    .semantics { contentDescription = loadingDescription },
                color = FinanziaColors.TextoSobreColor,
                strokeWidth = FinanziaSizes.GrosorIndicador
            )
        } else {
            Text(
                text = text,
                style = FinanziaTypography.Boton,
                color = FinanziaColors.TextoSobreColor
            )
            if (showArrow) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null, // decorativa: el texto ya dice qué hace
                    tint = FinanziaColors.TextoSobreColor,
                    modifier = Modifier
                        .padding(start = FinanziaSpacing.S)
                        .size(FinanziaSizes.Icono)
                )
            }
        }
    }
}

@Preview(name = "Botones")
@Composable
private fun FinanziaButtonPreview() {
    PreviewFondo {
        FinanziaButton(text = "Entrar", onClick = {})
        FinanziaButton(text = "Guardar gasto y finalizar", onClick = {}, solidColor = FinanziaColors.Azul)
        FinanziaButton(text = "Entrar", onClick = {}, loading = true)
        FinanziaButton(text = "Crear cuenta", onClick = {}, enabled = false)
    }
}
