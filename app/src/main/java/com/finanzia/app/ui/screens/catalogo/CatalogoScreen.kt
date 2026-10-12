package com.finanzia.app.ui.screens.catalogo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.finanzia.app.R
import com.finanzia.app.ui.components.FinanziaButton
import com.finanzia.app.ui.components.FinanziaLogo
import com.finanzia.app.ui.components.FinanziaTextField
import com.finanzia.app.ui.components.OptionCard
import com.finanzia.app.ui.components.StepPill
import com.finanzia.app.ui.components.TipCard
import com.finanzia.app.ui.theme.Acento
import com.finanzia.app.ui.theme.FinanziaColors
import com.finanzia.app.ui.theme.FinanziaSpacing
import com.finanzia.app.ui.theme.FinanziaTheme
import com.finanzia.app.ui.theme.FinanziaTypography

/**
 * Pantalla PROVISIONAL (F-01) para ver en el emulador todos los componentes juntos.
 * Cuando exista la navegación (F-03), MainActivity abrirá el login en su lugar.
 *
 * Aquí el estado vive en la pantalla con `remember` porque es solo una demostración;
 * en las pantallas reales vivirá en el ViewModel.
 */
@Composable
fun CatalogoScreen(modifier: Modifier = Modifier) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var opcionElegida by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FinanziaColors.Fondo)
            .verticalScroll(rememberScrollState())
            .padding(FinanziaSpacing.L),
        verticalArrangement = Arrangement.spacedBy(FinanziaSpacing.M)
    ) {
        FinanziaLogo(showTagline = true, modifier = Modifier.align(Alignment.CenterHorizontally))
        Text(
            text = stringResource(R.string.catalogo_titulo),
            style = FinanziaTypography.Titulo,
            color = FinanziaColors.Tinta
        )
        Text(
            text = stringResource(R.string.catalogo_descripcion),
            style = FinanziaTypography.Cuerpo,
            color = FinanziaColors.TextoSecundario
        )
        StepPill(text = stringResource(R.string.catalogo_paso), acento = Acento.Gasto)

        FinanziaTextField(
            value = correo,
            onValueChange = { correo = it },
            label = stringResource(R.string.catalogo_campo_correo),
            placeholder = stringResource(R.string.catalogo_campo_correo_ejemplo),
            leadingIcon = Icons.Rounded.Email
        )
        FinanziaTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = stringResource(R.string.catalogo_campo_contrasena),
            leadingIcon = Icons.Rounded.Lock,
            isPassword = true
        )
        FinanziaTextField(
            value = "ana@finanzia",
            onValueChange = {},
            label = stringResource(R.string.catalogo_campo_correo),
            leadingIcon = Icons.Rounded.Email,
            errorMessage = stringResource(R.string.catalogo_error_correo)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(FinanziaSpacing.SM)) {
            OptionCard(
                emoji = "🏠",
                title = stringResource(R.string.catalogo_opcion_titulo),
                description = stringResource(R.string.catalogo_opcion_descripcion),
                selected = opcionElegida == 0,
                onClick = { opcionElegida = 0 },
                acento = Acento.Gasto,
                modifier = Modifier.weight(1f)
            )
            OptionCard(
                emoji = "🍔",
                title = stringResource(R.string.catalogo_opcion2_titulo),
                description = stringResource(R.string.catalogo_opcion2_descripcion),
                selected = opcionElegida == 1,
                onClick = { opcionElegida = 1 },
                acento = Acento.Gasto,
                modifier = Modifier.weight(1f)
            )
        }

        TipCard(
            title = stringResource(R.string.catalogo_dato_titulo),
            text = stringResource(R.string.catalogo_dato_texto),
            acento = Acento.Gasto
        )

        FinanziaButton(text = stringResource(R.string.catalogo_boton_principal), onClick = {})
        FinanziaButton(
            text = stringResource(R.string.catalogo_boton_solido),
            onClick = {},
            solidColor = FinanziaColors.Azul
        )
        FinanziaButton(
            text = stringResource(R.string.catalogo_boton_cargando),
            onClick = {},
            loading = true
        )
    }
}

@Preview(name = "Catálogo", heightDp = 1400)
@Composable
private fun CatalogoScreenPreview() {
    FinanziaTheme {
        CatalogoScreen()
    }
}
