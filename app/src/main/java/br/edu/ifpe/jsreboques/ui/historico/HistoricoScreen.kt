// O que faz: Exibe o histórico unificado de vendas, compras e ajustes de estoque.
// Por que existe: Gerencia a visualização do histórico de movimentações da loja.
// O que quebra se sumir: O aplicativo perde a rastreabilidade e a listagem de movimentações.

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
import br.edu.ifpe.jsreboques.ui.UiState
import br.edu.ifpe.jsreboques.ui.components.*
import br.edu.ifpe.jsreboques.ui.theme.VerdeFundo
import br.edu.ifpe.jsreboques.ui.theme.VerdeTexto
import br.edu.ifpe.jsreboques.util.inicioDoMes
import br.edu.ifpe.jsreboques.util.mesAnoAtual
import br.edu.ifpe.jsreboques.util.paraDataHora
import br.edu.ifpe.jsreboques.util.paraReais
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.IOException

data class ResumoMensal(
    val mes: String = "",
    val vendasMes: Int = 0,
    val unidadesMes: Int = 0,
    val totalMesCents: Long = 0
)

class HistoricoViewModel(private val operacoes: OperacaoRepository) : ViewModel() {

    private val _filtro = MutableStateFlow<TipoMovimentacao?>(null)
    val filtro: StateFlow<TipoMovimentacao?> = _filtro.asStateFlow()

    private val _estadoUi = MutableStateFlow<UiState<Movimentacao>>(UiState.Carregando)
    val estadoUi: StateFlow<UiState<Movimentacao>> = _estadoUi.asStateFlow()

    private val _resumo = MutableStateFlow(ResumoMensal())
    val resumo: StateFlow<ResumoMensal> = _resumo.asStateFlow()

    init {
        carregar()
    }

    fun carregar() {
        _estadoUi.value = UiState.Carregando
        viewModelScope.launch {
            try {
                combine(operacoes.observarHistorico(), _filtro) { lista, f ->
                    // 1. Calcular métricas do mês (sempre sobre a lista completa)
                    val vendasDoMes = lista.filter {
                        it.tipo == TipoMovimentacao.VENDA && it.timestamp >= inicioDoMes()
                    }
                    _resumo.value = ResumoMensal(
                        mes = mesAnoAtual(),
                        vendasMes = vendasDoMes.size,
                        unidadesMes = vendasDoMes.sumOf { -it.delta },
                        totalMesCents = vendasDoMes.sumOf { it.valorCents ?: 0L }
                    )

                    // 2. Filtrar e ordenar (mais recentes primeiro)
                    val filtrados = if (f == null) lista else lista.filter { it.tipo == f }
                    filtrados.sortedByDescending { it.timestamp }
                }.collect { itens ->
                    if (itens.isEmpty()) {
                        _estadoUi.value = UiState.Vazio
                    } else {
                        _estadoUi.value = UiState.Sucesso(itens)
                    }
                }
            } catch (e: IOException) {
                _estadoUi.value = UiState.Erro("Sem conexão. Verifique a internet e tente novamente.")
            } catch (e: Exception) {
                _estadoUi.value = UiState.Erro("Não foi possível carregar os dados.")
            }
        }
    }

    fun aoFiltrar(f: TipoMovimentacao?) {
        _filtro.value = f
    }
}

@Composable
fun HistoricoScreen(
    aoVoltar: () -> Unit,
    vm: HistoricoViewModel = appViewModel { HistoricoViewModel(it.operacaoRepository) }
) {
    val estadoUi by vm.estadoUi.collectAsStateWithLifecycle()
    val filtro by vm.filtro.collectAsStateWithLifecycle()
    val resumo by vm.resumo.collectAsStateWithLifecycle()

    val opcoes = listOf("Todos", "Vendas", "Compras", "Ajustes")
    val selecionado = when (filtro) {
        TipoMovimentacao.VENDA -> "Vendas"
        TipoMovimentacao.COMPRA -> "Compras"
        TipoMovimentacao.AJUSTE -> "Ajustes"
        null -> "Todos"
    }

    when (val atual = estadoUi) {
        UiState.Carregando -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        UiState.Vazio -> {
            MensagemCentral(
                texto = "Nenhuma movimentação registrada ainda.",
                rotuloBotao = "Recarregar",
                aoClicar = { vm.carregar() }
            )
        }
        is UiState.Erro -> {
            MensagemCentral(
                texto = atual.mensagem,
                rotuloBotao = "Tentar Novamente",
                aoClicar = { vm.carregar() }
            )
        }
        is UiState.Sucesso -> {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { CabecalhoTela("Histórico", "Compras, vendas e ajustes", aoVoltar) }
                item {
                    CartaoSuave {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(resumo.mes, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "${resumo.vendasMes} vendas • ${resumo.unidadesMes} unidades",
                                    modifier = Modifier.weight(1f),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(resumo.totalMesCents.paraReais(), fontWeight = FontWeight.Bold)
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
                        "${atual.itens.size} movimentações • Mais recentes primeiro",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(atual.itens, key = { it.chave }) { m -> CartaoMovimentacao(m) }
            }
        }
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
