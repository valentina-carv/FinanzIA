package com.finanzia.app.ui.theme

import androidx.compose.ui.graphics.toArgb
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

/**
 * Comprueba que los colores de `FinanziaColors` son exactamente los de la
 * tabla de CLAUDE.md. Si alguien cambia un color por error, esta prueba falla.
 */
class FinanziaColorsTest {

    private fun hex(color: androidx.compose.ui.graphics.Color): String =
        String.format(Locale.ROOT, "#%06X", color.toArgb() and 0xFFFFFF)

    @Test
    fun paleta_coincide_con_claude_md() {
        val esperado = mapOf(
            "Fondo" to "#F8F9FD",
            "Superficie" to "#FFFFFF",
            "Tinta" to "#0F1B3A",
            "TextoSecundario" to "#64748B",
            "TextoTenue" to "#94A3B8",
            "Borde" to "#E2E8F0",
            "Verde" to "#10B981",
            "VerdeSuave" to "#ECFDF5",
            "Azul" to "#0062F0",
            "AzulSuave" to "#EAF2FF",
            "Coral" to "#E23D68",
            "CoralSuave" to "#FFF1F3",
            "Violeta" to "#7C5CFF",
            "VioletaSuave" to "#F1EDFF",
            "Ambar" to "#FFB547"
        )
        val real = with(FinanziaColors) {
            mapOf(
                "Fondo" to hex(Fondo),
                "Superficie" to hex(Superficie),
                "Tinta" to hex(Tinta),
                "TextoSecundario" to hex(TextoSecundario),
                "TextoTenue" to hex(TextoTenue),
                "Borde" to hex(Borde),
                "Verde" to hex(Verde),
                "VerdeSuave" to hex(VerdeSuave),
                "Azul" to hex(Azul),
                "AzulSuave" to hex(AzulSuave),
                "Coral" to hex(Coral),
                "CoralSuave" to hex(CoralSuave),
                "Violeta" to hex(Violeta),
                "VioletaSuave" to hex(VioletaSuave),
                "Ambar" to hex(Ambar)
            )
        }
        assertEquals(esperado, real)
    }

    @Test
    fun el_violeta_es_el_acento_de_la_ia() {
        assertEquals(FinanziaColors.Violeta, Acento.IA.color)
        assertEquals(FinanziaColors.Verde, Acento.Ingreso.color)
        assertEquals(FinanziaColors.Coral, Acento.Gasto.color)
    }
}
