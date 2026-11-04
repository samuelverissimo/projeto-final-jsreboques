package br.edu.ifpe.jsreboques.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.edu.ifpe.jsreboques.ui.features.clientes.ClientesScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavTarget.HOME.route
    ) {
        composable(NavTarget.HOME.route) {
            HomeScreen(onNavigateToClientes = {
                navController.navigate(NavTarget.CLIENTES.route)
            })
        }
        composable(NavTarget.CLIENTES.route) {
            ClientesScreen()
        }
    }
}

@Composable
fun HomeScreen(onNavigateToClientes: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Button(onClick = onNavigateToClientes) {
            Text(text = "Ver Clientes")
        }
    }
}
