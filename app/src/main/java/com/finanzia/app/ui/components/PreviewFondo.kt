package com.finanzia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.finanzia.app.ui.theme.FinanziaColors
import com.finanzia.app.ui.theme.FinanziaSpacing
import com.finanzia.app.ui.theme.FinanziaTheme

/**
 * Envoltura para los @Preview: aplica el tema y el fondo de FinanzIA,
 * así cada vista previa se ve como en la app.
 */
@Composable
internal fun PreviewFondo(content: @Composable ColumnScope.() -> Unit) {
    FinanziaTheme {
        Column(
            modifier = Modifier
                .background(FinanziaColors.Fondo)
                .padding(FinanziaSpacing.M),
            verticalArrangement = Arrangement.spacedBy(FinanziaSpacing.M),
            content = content
        )
    }
}
