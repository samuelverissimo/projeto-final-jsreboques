package br.edu.ifpe.jsreboques.ui.historico

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NorthEast
import androidx.compose.material.icons.outlined.SouthWest
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import br.edu.ifpe.jsreboques.data.repository.OperacaoRepository
import br.edu.ifpe.jsreboques.domain.Movimentacao
import br.edu.ifpe.jsreboques.domain.TipoMovimentacao
import br.edu.ifpe.jsreboques.ui.components.*
import br.edu.ifpe.jsreboques.ui.theme.VerdeFundo
import br.edu.ifpe.jsreboques.ui.theme.VerdeTexto
import br.edu.ifpe.jsreboques.util.inicioDoMes
import br.edu.ifpe.jsreboques.util.mesAnoAtual
import br.edu.ifpe.jsreboques.util.paraDataHora
import br.edu.ifpe.jsreboques.util.paraReais
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HistoricoUiState(
    val filtro: TipoMovimentacao? = null,
    val itens: List<Movimentacao> = emptyList(),
    val mes: String = "",
    val vendasMes: Int = 0,
    val unidadesMes: Int = 0,
    val totalMesCents: Long = 0
)

class HistoricoViewModel(operacoes: OperacaoRepository) : ViewModel() {

    private val filtro = MutableStateFlow<TipoMovimentacao?>(null)

    val estado: StateFlow<HistoricoUiState> =
        combine(operacoes.observarHistorico(), filtro) { lista, f ->
            val vendasDoMes = lista.filter {
                it.tipo == TipoMovimentacao.VENDA && it.timestamp >= inicioDoMes()
            }
            HistoricoUiState(
                filtro = f,
                itens = if (f == null) lista else lista.filter { it.tipo == f },
                mes = mesAnoAtual(),
                vendasMes = vendasDoMes.size,
                unidadesMes = vendasDoMes.sumOf { -it.delta },
                totalMesCents = vendasDoMes.sumOf { it.valorCents ?: 0L }
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoricoUiState())

    fun aoFiltrar(f: TipoMovimentacao?) { filtro.value = f }
}

@Composable
fun HistoricoScreen(
    aoVoltar: () -> Unit,
    vm: HistoricoViewModel = appViewModel<HistoricoViewModel> { HistoricoViewModel(it.operacaoRepository) }
) {
    val e by vm.estado.collectAsStateWithLifecycle()
    val opcoes = listOf("Todos", "Vendas", "Compras", "Ajustes")
    val selecionado = when (e.filtro) {
        TipoMovimentacao.VENDA -> "Vendas"
        TipoMovimentacao.COMPRA -> "Compras"
        TipoMovimentacao.AJUSTE -> "Ajustes"
        null -> "Todos"
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { CabecalhoTela("Histórico", "Compras, vendas e ajustes", aoVoltar) }
        item {
            CartaoSuave {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(e.mes, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${e.vendasMes} vendas • ${e.unidadesMes} unidades",
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(e.totalMesCents.paraReais(), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            FiltroChips(opcoes, selecionado) {
                vm.aoFiltrar(
                    when (it) {
                        "Vendas" -> TipoMovimentacao.VENDA
                        "Compras" -> TipoMovimentacao.COMPRA
                        "Ajustes" -> TipoMovimentacao.AJUSTE
                        else -> null
                    }
                )
            }
        }
        item {
            Text(
                "${e.itens.size} movimentações • Mais recentes primeiro",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (e.itens.isEmpty()) {
            item { Text("Nenhuma movimentação registrada ainda.") }
        }
        items(e.itens, key = { it.chave }) { m -> CartaoMovimentacao(m) }
    }
}

@Composable
private fun CartaoMovimentacao(m: Movimentacao) {
    Cartao(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val icone = when (m.tipo) {
                    TipoMovimentacao.VENDA -> Icons.Outlined.NorthEast
                    TipoMovimentacao.COMPRA -> Icons.Outlined.SouthWest
                    TipoMovimentacao.AJUSTE -> Icons.Outlined.Tune
                }
                Icon(icone, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("${m.tipo.rotulo} • ${m.codigo}", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text(m.timestamp.paraDataHora(), style = MaterialTheme.typography.bodySmall)
            }
            Text(m.reboqueNome, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(m.detalhe, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(verticalAlignment = Alignment.CenterVertically) {
                val entrada = m.delta > 0
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = if (entrada) VerdeFundo else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        (if (entrada) "+${m.delta}" else "−${-m.delta}") + " un.",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = if (entrada) VerdeTexto else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(m.valorCents?.paraReais() ?: "Sem valor financeiro", fontWeight = FontWeight.Bold)
            }
            Text(
                "Estoque antes → depois: ${m.estoqueAntes} → ${m.estoqueDepois} un.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
