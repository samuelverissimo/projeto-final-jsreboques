package br.edu.ifpe.jsreboques.ui.screens.compra

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.edu.ifpe.jsreboques.data.local.relation.CompraComDetalhes
import br.edu.ifpe.jsreboques.data.repository.JsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CompraConfirmadaUiState(
    val compraComDetalhes: CompraComDetalhes? = null,
    val totalEstoqueGeral: Int = 18
)

class CompraConfirmadaViewModel(
    private val repository: JsRepository,
    private val compraId: Long
) : ViewModel() {

    private val _compra = MutableStateFlow<CompraComDetalhes?>(null)

    init {
        viewModelScope.launch {
            _compra.value = repository.getCompraComDetalhesById(compraId)
        }
    }

    val uiState: StateFlow<CompraConfirmadaUiState> = combine(
        _compra,
        repository.reboquesComEstoque
    ) { compra, reboques ->
        val totalEstoque = reboques.sumOf { it.quantidadeEstoque }

        CompraConfirmadaUiState(
            compraComDetalhes = compra,
            totalEstoqueGeral = totalEstoque
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CompraConfirmadaUiState()
    )

    class Factory(
        private val repository: JsRepository,
        private val compraId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CompraConfirmadaViewModel(repository, compraId) as T
        }
    }
}
