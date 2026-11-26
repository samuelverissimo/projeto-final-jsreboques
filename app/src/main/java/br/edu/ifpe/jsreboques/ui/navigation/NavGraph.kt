package br.edu.ifpe.jsreboques.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import br.edu.ifpe.jsreboques.data.repository.JsRepository
import br.edu.ifpe.jsreboques.ui.screens.clientes.ClientesScreen
import br.edu.ifpe.jsreboques.ui.screens.clientes.ClientesViewModel
import br.edu.ifpe.jsreboques.ui.screens.compra.CompraConfirmadaScreen
import br.edu.ifpe.jsreboques.ui.screens.compra.CompraConfirmadaViewModel
import br.edu.ifpe.jsreboques.ui.screens.compra.NovaCompraScreen
import br.edu.ifpe.jsreboques.ui.screens.compra.NovaCompraViewModel
import br.edu.ifpe.jsreboques.ui.screens.estoque.EstoqueScreen
import br.edu.ifpe.jsreboques.ui.screens.estoque.EstoqueViewModel
import br.edu.ifpe.jsreboques.ui.screens.historico.HistoricoScreen
import br.edu.ifpe.jsreboques.ui.screens.historico.HistoricoViewModel
import br.edu.ifpe.jsreboques.ui.screens.painel.PainelScreen
import br.edu.ifpe.jsreboques.ui.screens.painel.PainelViewModel
import br.edu.ifpe.jsreboques.ui.screens.reboques.ReboquesScreen
import br.edu.ifpe.jsreboques.ui.screens.reboques.ReboquesViewModel
import br.edu.ifpe.jsreboques.ui.screens.venda.NovaVendaScreen
import br.edu.ifpe.jsreboques.ui.screens.venda.NovaVendaViewModel
import br.edu.ifpe.jsreboques.ui.screens.venda.VendaConcluidaScreen
import br.edu.ifpe.jsreboques.ui.screens.venda.VendaConcluidaViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    repository: JsRepository
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navigateToTarget: (NavTarget) -> Unit = { target ->
        navController.navigate(target.route) {
            popUpTo(NavTarget.PAINEL.route) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavTarget.PAINEL.route
    ) {
        composable(NavTarget.PAINEL.route) {
            val vm: PainelViewModel = viewModel(factory = PainelViewModel.Factory(repository))
            PainelScreen(
                viewModel = vm,
                currentRoute = currentRoute,
                onNavigate = navigateToTarget
            )
        }

        composable(NavTarget.REBOQUES.route) {
            val vm: ReboquesViewModel = viewModel(factory = ReboquesViewModel.Factory(repository))
            ReboquesScreen(
                viewModel = vm,
                currentRoute = currentRoute,
                onNavigate = navigateToTarget
            )
        }

        composable(NavTarget.ESTOQUE.route) {
            val vm: EstoqueViewModel = viewModel(factory = EstoqueViewModel.Factory(repository))
            EstoqueScreen(
                viewModel = vm,
                currentRoute = currentRoute,
                onNavigate = navigateToTarget
            )
        }

        composable(NavTarget.CLIENTES.route) {
            val vm: ClientesViewModel = viewModel(factory = ClientesViewModel.Factory(repository))
            ClientesScreen(
                viewModel = vm,
                currentRoute = currentRoute,
                onNavigate = navigateToTarget
            )
        }

        composable(NavTarget.NOVA_VENDA.route) {
            val vm: NovaVendaViewModel = viewModel(factory = NovaVendaViewModel.Factory(repository))
            NovaVendaScreen(
                viewModel = vm,
                currentRoute = currentRoute,
                onNavigate = navigateToTarget,
                onBackClick = { navController.popBackStack() },
                onVendaConcluida = { vendaId ->
                    navController.navigate(NavTarget.createVendaConcluidaRoute(vendaId)) {
                        popUpTo(NavTarget.NOVA_VENDA.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = NavTarget.VENDA_CONCLUIDA.route,
            arguments = listOf(navArgument("vendaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vendaId = backStackEntry.arguments?.getLong("vendaId") ?: 1L
            val vm: VendaConcluidaViewModel = viewModel(factory = VendaConcluidaViewModel.Factory(repository, vendaId))
            VendaConcluidaScreen(
                viewModel = vm,
                currentRoute = currentRoute,
                onNavigate = navigateToTarget,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavTarget.NOVA_COMPRA.route) {
            val vm: NovaCompraViewModel = viewModel(factory = NovaCompraViewModel.Factory(repository))
            NovaCompraScreen(
                viewModel = vm,
                currentRoute = currentRoute,
                onNavigate = navigateToTarget,
                onBackClick = { navController.popBackStack() },
                onCompraConfirmada = { compraId ->
                    navController.navigate(NavTarget.createCompraConfirmadaRoute(compraId)) {
                        popUpTo(NavTarget.NOVA_COMPRA.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = NavTarget.COMPRA_CONFIRMADA.route,
            arguments = listOf(navArgument("compraId") { type = NavType.LongType })
        ) { backStackEntry ->
            val compraId = backStackEntry.arguments?.getLong("compraId") ?: 1L
            val vm: CompraConfirmadaViewModel = viewModel(factory = CompraConfirmadaViewModel.Factory(repository, compraId))
            CompraConfirmadaScreen(
                viewModel = vm,
                currentRoute = currentRoute,
                onNavigate = navigateToTarget,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavTarget.HISTORICO.route) {
            val vm: HistoricoViewModel = viewModel(factory = HistoricoViewModel.Factory(repository))
            HistoricoScreen(
                viewModel = vm,
                currentRoute = currentRoute,
                onNavigate = navigateToTarget,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
