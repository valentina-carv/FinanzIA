package com.finanzia.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.finanzia.app.R
import com.finanzia.app.ui.theme.FinanziaColors
import com.finanzia.app.ui.theme.FinanziaTypography

/**
 * Logo de FinanzIA (decisión D-11): "FINANZ" en tinta, "IA" en verde y un ✦ verde.
 * Con `showTagline = true` agrega debajo "Tu asistente financiero con IA".
 *
 * `buildAnnotatedString` permite pintar partes de un mismo texto con estilos distintos.
 */
@Composable
fun FinanziaLogo(
    modifier: Modifier = Modifier,
    showTagline: Boolean = false
) {
    val description = stringResource(R.string.logo_descripcion)
    val logoText = buildAnnotatedString {
        withStyle(SpanStyle(color = FinanziaColors.Tinta)) {
            append(stringResource(R.string.logo_finanz))
        }
        withStyle(SpanStyle(color = FinanziaColors.Verde)) {
            append(stringResource(R.string.logo_ia))
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Para TalkBack el logo se lee como una sola palabra: "FinanzIA".
        Row(modifier = Modifier.clearAndSetSemantics { contentDescription = description }) {
            Text(text = logoText, style = FinanziaTypography.Logo)
            Text(
                text = stringResource(R.string.logo_destello),
                style = FinanziaTypography.Subtitulo,
                color = FinanziaColors.Verde
            )
        }
        if (showTagline) {
            Text(
                text = stringResource(R.string.eslogan),
                style = FinanziaTypography.Subtitulo,
                color = FinanziaColors.Verde
            )
        }
    }
}

@Preview(name = "Logo")
@Composable
private fun FinanziaLogoPreview() {
    PreviewFondo {
        FinanziaLogo()
        FinanziaLogo(showTagline = true)
    }
}
