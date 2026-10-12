package com.finanzia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.finanzia.app.ui.screens.catalogo.CatalogoScreen
import com.finanzia.app.ui.theme.FinanziaColors
import com.finanzia.app.ui.theme.FinanziaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanziaTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = FinanziaColors.Fondo
                ) { innerPadding ->
                    // Provisional (F-01): muestra el catálogo de componentes.
                    CatalogoScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
