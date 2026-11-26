package br.edu.ifpe.jsreboques

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import br.edu.ifpe.jsreboques.data.local.AppDatabase
import br.edu.ifpe.jsreboques.data.repository.JsRepository
import br.edu.ifpe.jsreboques.ui.navigation.NavGraph
import br.edu.ifpe.jsreboques.ui.theme.JSReboquesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = JsRepository(AppDatabase.getInstance(applicationContext))
        setContent {
            JSReboquesTheme {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    repository = repository
                )
            }
        }
    }
}
