package br.edu.ifpe.jsreboques.ui.screens.compra

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.edu.ifpe.jsreboques.data.local.relation.ReboqueComEstoque
import br.edu.ifpe.jsreboques.data.repository.JsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NovaCompraUiState(
    val reboques: List<ReboqueComEstoque> = emptyList(),
    val selectedReboque: ReboqueComEstoque? = null,
    val quantidadeText: String = "3",
    val valorUnitarioCents: Long = 440000L,
    val showSnackbar: Boolean = false,
    val errorMessage: String? = null
) {
    val quantidade: Int
        get() = quantidadeText.toIntOrNull() ?: 0

    val valorTotalCents: Long
        get() = valorUnitarioCents * quantidade

    val saldoAtual: Int
        get() = selectedReboque?.quantidadeEstoque ?: 0

    val saldoApos: Int
        get() = saldoAtual + quantidade
}

class NovaCompraViewModel(private val repository: JsRepository) : ViewModel() {

    private val _selectedReboqueId = MutableStateFlow<Long?>(null)
    private val _quantidadeText = MutableStateFlow("3")
    private val _valorUnitarioCents = MutableStateFlow(440000L)
    private val _showSnackbar = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    private val _formState = combine(
        _selectedReboqueId,
        _quantidadeText,
        _valorUnitarioCents,
        _showSnackbar,
        _errorMessage
    ) { rId, qText, vCents, snackbar, error ->
        Tuple5(rId, qText, vCents, snackbar, error)
    }

    val uiState: StateFlow<NovaCompraUiState> = combine(
        repository.reboquesComEstoque,
        _formState
    ) { reboques, (rId, qText, vCents, snackbar, error) ->
        val selectedReboque = reboques.find { it.reboque.id == rId } ?: reboques.find { it.reboque.modelo == "MT-02" } ?: reboques.firstOrNull()

        // Set default purchase cost if reboque selected
        val unitPrice = if (vCents == 440000L && selectedReboque != null) {
            selectedReboque.reboque.valorCompraCents
        } else vCents

        NovaCompraUiState(
            reboques = reboques,
            selectedReboque = selectedReboque,
            quantidadeText = qText,
            valorUnitarioCents = unitPrice,
            showSnackbar = snackbar,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NovaCompraUiState()
    )

    fun onReboqueSelected(reboque: ReboqueComEstoque) {
        _selectedReboqueId.value = reboque.reboque.id
        _valorUnitarioCents.value = reboque.reboque.valorCompraCents
    }

    fun onQuantidadeChanged(newText: String) {
        _quantidadeText.value = newText.filter { it.isDigit() }
    }

    fun onValorUnitarioCentsChanged(cents: Long) {
        _valorUnitarioCents.value = cents
    }

    fun dismissSnackbar() {
        _showSnackbar.value = false
    }

    fun confirmarCompra(onSuccess: (compraId: Long) -> Unit) {
        viewModelScope.launch {
            val state = uiState.value
            val reboque = state.selectedReboque ?: return@launch
            val quantidade = state.quantidade
            val valorUnitario = state.valorUnitarioCents

            if (quantidade <= 0 || valorUnitario <= 0) {
                _errorMessage.value = "Informe a quantidade e o valor unitário válidos."
                return@launch
            }

            val result = repository.registrarCompra(
                reboqueId = reboque.reboque.id,
                quantidade = quantidade,
                valorUnitarioCents = valorUnitario
            )

            if (result.isSuccess) {
                val compraId = result.getOrThrow()
                onSuccess(compraId)
            } else {
                _errorMessage.value = "Não foi possível realizar a operação. Tente novamente."
                _showSnackbar.value = true
            }
        }
    }

    class Factory(private val repository: JsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NovaCompraViewModel(repository) as T
        }
    }
}

private data class Tuple5<A, B, C, D, E>(
    val first: A, val second: B, val third: C, val fourth: D, val fifth: E
)
