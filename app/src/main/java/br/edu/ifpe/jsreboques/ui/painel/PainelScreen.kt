package br.edu.ifpe.jsreboques.ui.painel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import br.edu.ifpe.jsreboques.data.local.entity.ResumoVendas
import br.edu.ifpe.jsreboques.data.local.model.ReboqueComEstoque
import br.edu.ifpe.jsreboques.data.repository.OperacaoRepository
import br.edu.ifpe.jsreboques.data.repository.ReboqueRepository
import br.edu.ifpe.jsreboques.domain.StatusEstoque
import br.edu.ifpe.jsreboques.domain.statusDoEstoque
import br.edu.ifpe.jsreboques.ui.components.Cartao
import br.edu.ifpe.jsreboques.ui.components.CabecalhoTela
import br.edu.ifpe.jsreboques.ui.components.EtiquetaStatus
import br.edu.ifpe.jsreboques.ui.components.appViewModel
import br.edu.ifpe.jsreboques.ui.navigation.Rotas
import br.edu.ifpe.jsreboques.ui.theme.*
import br.edu.ifpe.jsreboques.util.dataPorExtenso
import br.edu.ifpe.jsreboques.util.mesAnoAtual
import br.edu.ifpe.jsreboques.util.paraReais
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

private const val NOME_USUARIO = "João" // TODO: vir do Login (F7) quando existir

data class PainelUiState(
    val vendasMesCents: Long = 0,
    val vendas: Int = 0,
    val unidadesVendidas: Int = 0,
    val totalEstoque: Int = 0,
    val modelos: Int = 0,
    val baixos: Int = 0,
    val zerados: Int = 0,
    val atencao: List<ReboqueComEstoque> = emptyList()
)

class PainelViewModel(
    reboques: ReboqueRepository,
    operacoes: OperacaoRepository
) : ViewModel() {

    val estado: StateFlow<PainelUiState> =
        combine(reboques.observarTodos(), operacoes.observarResumoMes()) { lista: List<ReboqueComEstoque>, resumo: ResumoVendas ->
            val precisaAtencao = lista.filter { statusDoEstoque(it.quantidade) != StatusEstoque.NORMAL }
            PainelUiState(
                vendasMesCents = resumo.totalCents,
                vendas = resumo.vendas,
                unidadesVendidas = resumo.unidades,
                totalEstoque = lista.sumOf { it.quantidade },
                modelos = lista.size,
                baixos = lista.count { statusDoEstoque(it.quantidade) == StatusEstoque.BAIXO },
                zerados = lista.count { statusDoEstoque(it.quantidade) == StatusEstoque.ZERO },
                atencao = precisaAtencao.sortedBy { it.quantidade }
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PainelUiState())
}

private fun saudacao(): String {
    val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hora < 12 -> "Bom dia"
        hora < 18 -> "Boa tarde"
        else -> "Boa noite"
    }
}

private class Atalho(val rotulo: String, val icone: ImageVector, val rota: String)

private val atalhos = listOf(
    Atalho("Reboques", Icons.Outlined.LocalShipping, Rotas.REBOQUES),
    Atalho("Estoque", Icons.Outlined.Inventory2, Rotas.ESTOQUE),
    Atalho("Nova venda", Icons.Outlined.ShoppingCart, Rotas.NOVA_VENDA),
    Atalho("Nova compra", Icons.Outlined.AddShoppingCart, Rotas.NOVA_COMPRA),
    Atalho("Clientes", Icons.Outlined.People, Rotas.CLIENTES),
    Atalho("Tarefas", Icons.Outlined.Checklist, Rotas.TAREFAS),
    Atalho("Fornecedores", Icons.Outlined.Business, Rotas.FORNECEDORES),
    Atalho("Histórico", Icons.Outlined.History, Rotas.HISTORICO)
)

@Composable
fun PainelScreen(
    aoNavegar: (String) -> Unit,
    vm: PainelViewModel = appViewModel<PainelViewModel> {
        PainelViewModel(it.reboqueRepository, it.operacaoRepository)
    }
) {
    val e by vm.estado.collectAsStateWithLifecycle()

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item { CabecalhoTela("JS Reboques", "Gestão de fábrica e loja") }
        item {
            Column(
                Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        "${saudacao()}, $NOME_USUARIO",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(dataPorExtenso(), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // Vendas do mês
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AzulPrimario)
                ) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row {
                            Text("Vendas do mês", color = Color.White, modifier = Modifier.weight(1f))
                            Text(mesAnoAtual(), color = AzulClaro, style = MaterialTheme.typography.labelMedium)
                        }
                        Text(
                            e.vendasMesCents.paraReais(),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${e.vendas} vendas realizadas • ${e.unidadesVendidas} unidades vendidas",
                            color = AzulClaro,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Cartao(Modifier.weight(1f)) {
                        Column(Modifier.padding(16.dp)) {
                            Icon(Icons.Outlined.Inventory2, null)
                            Text("${e.totalEstoque}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Text("Total em estoque")
                            Text(
                                "${e.modelos} modelos cadastrados",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Card(
                        Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AmareloFundo)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Icon(Icons.Outlined.Warning, null, tint = AmareloTexto)
                            Text(
                                "${e.baixos + e.zerados}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = AmareloTexto
                            )
                            Text("Alerta de estoque baixo", color = AmareloTexto)
                            Text(
                                "${e.baixos} baixo • ${e.zerados} zerado",
                                style = MaterialTheme.typography.bodySmall,
                                color = AmareloTexto
                            )
                        }
                    }
                }

                Text("Acesso rápido", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                atalhos.chunked(3).forEach { linha ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        linha.forEach { atalho ->
                            Cartao(Modifier.weight(1f), aoClicar = { aoNavegar(atalho.rota) }) {
                                Column(
                                    Modifier.fillMaxWidth().padding(vertical = 18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(atalho.icone, contentDescription = null)
                                    Text(atalho.rotulo, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Precisa de atenção",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { aoNavegar(Rotas.ESTOQUE) }) { Text("Ver estoque") }
                }
                Cartao(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (e.atencao.isEmpty()) {
                            Text("Nenhum item com estoque baixo.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        e.atencao.forEach { item ->
                            val status = statusDoEstoque(item.quantidade)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.reboque.nome, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                EtiquetaStatus(status, "${status.rotulo} • ${item.quantidade} un.")
                            }
                        }
                    }
                }
            }
        }
    }
}
