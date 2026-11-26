package br.edu.ifpe.jsreboques.ui.screens.historico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.edu.ifpe.jsreboques.data.repository.JsRepository
import br.edu.ifpe.jsreboques.ui.components.MovimentacaoUi
import br.edu.ifpe.jsreboques.util.DateFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HistoricoUiState(
    val movimentacoes: List<MovimentacaoUi> = emptyList(),
    val filteredMovimentacoes: List<MovimentacaoUi> = emptyList(),
    val selectedFilter: String = "Todos",
    val filterOptions: List<String> = listOf("Todos", "Vendas", "Compras", "Ajustes"),
    val mesAnoTitle: String = "Outubro de 2026",
    val totalVendasCount: Int = 4,
    val totalUnidadesVendidas: Int = 6,
    val totalVendasCents: Long = 4074000L
)

class HistoricoViewModel(private val repository: JsRepository) : ViewModel() {

    private val _selectedFilter = MutableStateFlow("Todos")

    private val _allMovimentacoes = combine(
        repository.vendasComDetalhes,
        repository.comprasComDetalhes,
        repository.ajustesComDetalhes
    ) { vendas, compras, ajustes ->
        val vUi = vendas.map { MovimentacaoUi.Venda(it) }
        val cUi = compras.map { MovimentacaoUi.Compra(it) }
        val aUi = ajustes.map { MovimentacaoUi.Ajuste(it) }

        (vUi + cUi + aUi).sortedByDescending { it.timestamp }
    }

    val uiState: StateFlow<HistoricoUiState> = combine(
        _allMovimentacoes,
        _selectedFilter,
        repository.vendasComDetalhes
    ) { all, filter, vendas ->
        val now = System.currentTimeMillis()
        val mesAno = if (vendas.isNotEmpty()) {
            DateFormatter.formatMonthYearTitle(vendas.first().venda.dataHoraTimestamp)
        } else DateFormatter.formatMonthYearTitle(now)

        val totalVendasCount = vendas.size
        val totalUnidades = vendas.sumOf { it.venda.quantidade }
        val totalVendasCents = vendas.sumOf { it.venda.valorTotalCents }

        val filtered = when (filter) {
            "Vendas" -> all.filterIsInstance<MovimentacaoUi.Venda>()
            "Compras" -> all.filterIsInstance<MovimentacaoUi.Compra>()
            "Ajustes" -> all.filterIsInstance<MovimentacaoUi.Ajuste>()
            else -> all
        }

        HistoricoUiState(
            movimentacoes = all,
            filteredMovimentacoes = filtered,
            selectedFilter = filter,
            mesAnoTitle = mesAno,
            totalVendasCount = totalVendasCount,
            totalUnidadesVendidas = totalUnidades,
            totalVendasCents = totalVendasCents
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoricoUiState()
    )

    fun onFilterSelected(filter: String) {
        _selectedFilter.value = filter
    }

    class Factory(private val repository: JsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HistoricoViewModel(repository) as T
        }
    }
}
