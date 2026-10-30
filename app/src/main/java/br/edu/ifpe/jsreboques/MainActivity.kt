package br.edu.ifpe.jsreboques

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.edu.ifpe.jsreboques.ui.navigation.AppRoot
import br.edu.ifpe.jsreboques.ui.theme.JsReboquesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JsReboquesTheme {
                AppRoot()
            }
        }
    }
}
