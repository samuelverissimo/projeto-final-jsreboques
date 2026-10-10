package br.edu.ifpe.jsreboques.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.edu.ifpe.jsreboques.ui.clientes.ClientesScreen
import br.edu.ifpe.jsreboques.ui.compra.NovaCompraScreen
import br.edu.ifpe.jsreboques.ui.estoque.EstoqueScreen
import br.edu.ifpe.jsreboques.ui.historico.HistoricoScreen
import br.edu.ifpe.jsreboques.ui.painel.PainelScreen
import br.edu.ifpe.jsreboques.ui.reboques.ReboquesScreen
import br.edu.ifpe.jsreboques.ui.theme.AzulClaro
import br.edu.ifpe.jsreboques.ui.theme.AzulPrimario
import br.edu.ifpe.jsreboques.ui.venda.NovaVendaScreen

object Rotas {
    const val PAINEL = "painel"
    const val REBOQUES = "reboques"
    const val ESTOQUE = "estoque"
    const val CLIENTES = "clientes"
    const val NOVA_VENDA = "nova_venda"
    const val NOVA_COMPRA = "nova_compra"
    const val HISTORICO = "historico"
}

private class ItemNavegacao(val rota: String, val rotulo: String, val icone: ImageVector)

@Composable
fun AppRoot() {
    val nav = rememberNavController()
    val rotaAtual = nav.currentBackStackEntryAsState().value?.destination?.route
    val itens = remember {
        listOf(
            ItemNavegacao(Rotas.PAINEL, "Painel", Icons.Outlined.GridView),
            ItemNavegacao(Rotas.REBOQUES, "Reboques", Icons.Outlined.LocalShipping),
            ItemNavegacao(Rotas.ESTOQUE, "Estoque", Icons.Outlined.Inventory2),
            ItemNavegacao(Rotas.CLIENTES, "Clientes", Icons.Outlined.People)
        )
    }

    fun irPara(rota: String) {
        if (itens.any { it.rota == rota }) {
            // Abas da barra inferior: não empilha telas repetidas
            nav.navigate(rota) {
                popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        } else {
            nav.navigate(rota) { launchSingleTop = true }
        }
    }

    Scaffold(
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                itens.forEach { item ->
                    NavigationBarItem(
                        selected = rotaAtual == item.rota,
                        onClick = { irPara(item.rota) },
                        icon = { Icon(item.icone, contentDescription = item.rotulo) },
                        label = { Text(item.rotulo) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AzulPrimario,
                            selectedTextColor = AzulPrimario,
                            indicatorColor = AzulClaro
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Rotas.PAINEL,
            modifier = Modifier.padding(padding)
        ) {
            composable(Rotas.PAINEL) { PainelScreen(aoNavegar = { irPara(it) }) }
            composable(Rotas.REBOQUES) { ReboquesScreen() }
            composable(Rotas.ESTOQUE) { EstoqueScreen() }
            composable(Rotas.CLIENTES) { ClientesScreen() }
            composable(Rotas.NOVA_VENDA) {
                NovaVendaScreen(
                    aoVoltar = { nav.popBackStack() },
                    aoVerEstoque = { irPara(Rotas.ESTOQUE) }
                )
            }
            composable(Rotas.NOVA_COMPRA) {
                NovaCompraScreen(
                    aoVoltar = { nav.popBackStack() },
                    aoVerEstoque = { irPara(Rotas.ESTOQUE) }
                )
            }
            composable(Rotas.HISTORICO) { HistoricoScreen(aoVoltar = { nav.popBackStack() }) }
        }
    }
}
