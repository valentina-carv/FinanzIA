package com.finanzia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.finanzia.app.R
import com.finanzia.app.ui.theme.FinanziaColors
import com.finanzia.app.ui.theme.FinanziaShapes
import com.finanzia.app.ui.theme.FinanziaSizes
import com.finanzia.app.ui.theme.FinanziaSpacing
import com.finanzia.app.ui.theme.FinanziaTypography

/**
 * Campo de texto de FinanzIA: tarjeta blanca con ícono a la izquierda,
 * etiqueta en MAYÚSCULAS arriba y el texto debajo.
 *
 * - `isPassword = true` oculta el texto y agrega el "ojo" para mostrarlo.
 * - Si `errorMessage` no es null, el borde se pone coral y el mensaje aparece debajo.
 *
 * El campo no guarda el texto: lo recibe en `value` y avisa cambios con
 * `onValueChange` (el estado vive en el ViewModel de cada pantalla).
 */
@Composable
fun FinanziaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next
) {
    // rememberSaveable: recuerda si la contraseña está visible aunque se gire el teléfono.
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val hasError = errorMessage != null
    val borderColor = if (hasError) FinanziaColors.Coral else FinanziaColors.Borde
    val borderWidth = if (hasError) FinanziaSizes.BordeSeleccion else FinanziaSizes.Borde

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = FinanziaSizes.AltoCampo)
                .background(FinanziaColors.Superficie, FinanziaShapes.Campo)
                .border(borderWidth, borderColor, FinanziaShapes.Campo)
                .padding(horizontal = FinanziaSpacing.M, vertical = FinanziaSpacing.SM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null, // la etiqueta ya describe el campo
                    tint = if (hasError) FinanziaColors.Coral else FinanziaColors.TextoSecundario,
                    modifier = Modifier
                        .padding(end = FinanziaSpacing.M)
                        .size(FinanziaSizes.Icono)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label.uppercase(),
                    style = FinanziaTypography.Etiqueta,
                    color = if (hasError) FinanziaColors.Coral else FinanziaColors.TextoSecundario
                )
                Box(modifier = Modifier.padding(top = FinanziaSpacing.XS)) {
                    // Placeholder: solo se ve mientras el campo está vacío.
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = FinanziaTypography.Cuerpo,
                            color = FinanziaColors.TextoTenue
                        )
                    }
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enabled,
                        singleLine = true,
                        textStyle = FinanziaTypography.Cuerpo.copy(color = FinanziaColors.Tinta),
                        cursorBrush = SolidColor(FinanziaColors.Azul),
                        visualTransformation = if (isPassword && !passwordVisible) {
                            PasswordVisualTransformation()
                        } else {
                            VisualTransformation.None
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
                            imeAction = imeAction
                        )
                    )
                }
            }

            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) {
                            Icons.Rounded.VisibilityOff
                        } else {
                            Icons.Rounded.Visibility
                        },
                        contentDescription = stringResource(
                            if (passwordVisible) R.string.ocultar_contrasena else R.string.mostrar_contrasena
                        ),
                        tint = FinanziaColors.TextoSecundario
                    )
                }
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = FinanziaTypography.CuerpoPequeno,
                color = FinanziaColors.Coral,
                modifier = Modifier.padding(start = FinanziaSpacing.M, top = FinanziaSpacing.XS)
            )
        }
    }
}

@Preview(name = "Campos de texto")
@Composable
private fun FinanziaTextFieldPreview() {
    PreviewFondo {
        FinanziaTextField(
            value = "",
            onValueChange = {},
            label = "Correo electrónico",
            placeholder = "tu@correo.com",
            leadingIcon = Icons.Rounded.Email
        )
        FinanziaTextField(
            value = "Finanzia2026",
            onValueChange = {},
            label = "Contraseña",
            leadingIcon = Icons.Rounded.Lock,
            isPassword = true
        )
        FinanziaTextField(
            value = "ana@finanzia",
            onValueChange = {},
            label = "Correo electrónico",
            leadingIcon = Icons.Rounded.Email,
            errorMessage = "El correo no es válido. Revisa que esté bien escrito."
        )
    }
}
