package br.edu.ifpe.jsreboques.ui.screens.venda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.edu.ifpe.jsreboques.data.local.relation.VendaComDetalhes
import br.edu.ifpe.jsreboques.data.repository.JsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class VendaConcluidaUiState(
    val vendaComDetalhes: VendaComDetalhes? = null,
    val totalEstoqueGeral: Int = 13,
    val totalVendasMesCents: Long = 5052000L
)

class VendaConcluidaViewModel(
    private val repository: JsRepository,
    private val vendaId: Long
) : ViewModel() {

    private val _venda = MutableStateFlow<VendaComDetalhes?>(null)

    init {
        viewModelScope.launch {
            _venda.value = repository.getVendaComDetalhesById(vendaId)
        }
    }

    val uiState: StateFlow<VendaConcluidaUiState> = combine(
        _venda,
        repository.reboquesComEstoque,
        repository.vendasComDetalhes
    ) { venda, reboques, vendas ->
        val totalEstoque = reboques.sumOf { it.quantidadeEstoque }
        val totalVendasMes = vendas.sumOf { it.venda.valorTotalCents }

        VendaConcluidaUiState(
            vendaComDetalhes = venda,
            totalEstoqueGeral = totalEstoque,
            totalVendasMesCents = totalVendasMes
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = VendaConcluidaUiState()
    )

    class Factory(
        private val repository: JsRepository,
        private val vendaId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return VendaConcluidaViewModel(repository, vendaId) as T
        }
    }
}
