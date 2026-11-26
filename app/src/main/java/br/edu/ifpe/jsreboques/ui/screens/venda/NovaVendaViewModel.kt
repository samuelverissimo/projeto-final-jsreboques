package br.edu.ifpe.jsreboques.ui.screens.venda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.edu.ifpe.jsreboques.data.local.entity.ClienteEntity
import br.edu.ifpe.jsreboques.data.local.relation.ReboqueComEstoque
import br.edu.ifpe.jsreboques.data.repository.JsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class VendaUiStatus {
    object Ready : VendaUiStatus()
    data class InsufficientStock(val available: Int) : VendaUiStatus()
    data class GenericError(val message: String) : VendaUiStatus()
    data class Success(val vendaId: Long) : VendaUiStatus()
}

data class NovaVendaUiState(
    val clientes: List<ClienteEntity> = emptyList(),
    val reboques: List<ReboqueComEstoque> = emptyList(),
    val selectedCliente: ClienteEntity? = null,
    val selectedReboque: ReboqueComEstoque? = null,
    val quantidadeText: String = "2",
    val status: VendaUiStatus = VendaUiStatus.Ready,
    val showSnackbar: Boolean = false
) {
    val quantidade: Int
        get() = quantidadeText.toIntOrNull() ?: 0

    val valorUnitarioCents: Long
        get() = selectedReboque?.reboque?.valorVendaCents ?: 0L

    val valorTotalCents: Long
        get() = valorUnitarioCents * quantidade

    val saldoAtual: Int
        get() = selectedReboque?.quantidadeEstoque ?: 0

    val saldoApos: Int
        get() = (saldoAtual - quantidade).coerceAtLeast(0)
}

class NovaVendaViewModel(private val repository: JsRepository) : ViewModel() {

    private val _selectedClienteId = MutableStateFlow<Long?>(null)
    private val _selectedReboqueId = MutableStateFlow<Long?>(null)
    private val _quantidadeText = MutableStateFlow("2")
    private val _status = MutableStateFlow<VendaUiStatus>(VendaUiStatus.Ready)
    private val _showSnackbar = MutableStateFlow(false)

    private val _dataState = combine(
        repository.clientes,
        repository.reboquesComEstoque
    ) { cList, rList ->
        Pair(cList, rList)
    }

    private val _formState = combine(
        _selectedClienteId,
        _selectedReboqueId,
        _quantidadeText,
        _status,
        _showSnackbar
    ) { cId, rId, qText, status, snackbar ->
        Tuple5(cId, rId, qText, status, snackbar)
    }

    val uiState: StateFlow<NovaVendaUiState> = combine(
        _dataState,
        _formState
    ) { (clientes, reboques), (cId, rId, qText, status, snackbar) ->
        val selectedCliente = clientes.find { it.id == cId } ?: clientes.firstOrNull()
        val selectedReboque = reboques.find { it.reboque.id == rId } ?: reboques.find { it.reboque.modelo == "UT-500" } ?: reboques.firstOrNull()

        val qtn = qText.toIntOrNull() ?: 0
        val saldoAvailable = selectedReboque?.quantidadeEstoque ?: 0

        val currentStatus = when {
            status is VendaUiStatus.Success -> status
            qtn > saldoAvailable -> VendaUiStatus.InsufficientStock(available = saldoAvailable)
            status is VendaUiStatus.GenericError -> status
            else -> VendaUiStatus.Ready
        }

        NovaVendaUiState(
            clientes = clientes,
            reboques = reboques,
            selectedCliente = selectedCliente,
            selectedReboque = selectedReboque,
            quantidadeText = qText,
            status = currentStatus,
            showSnackbar = snackbar
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NovaVendaUiState()
    )

    fun onClienteSelected(cliente: ClienteEntity) {
        _selectedClienteId.value = cliente.id
    }

    fun onReboqueSelected(reboque: ReboqueComEstoque) {
        _selectedReboqueId.value = reboque.reboque.id
        validateQuantity(_quantidadeText.value, reboque.quantidadeEstoque)
    }

    fun onQuantidadeChanged(newText: String) {
        val cleanText = newText.filter { it.isDigit() }
        _quantidadeText.value = cleanText
        _status.value = VendaUiStatus.Ready
        _showSnackbar.value = false
    }

    fun dismissSnackbar() {
        _showSnackbar.value = false
    }

    fun resetQuantity() {
        val state = uiState.value
        _quantidadeText.value = state.saldoAtual.toString()
        _status.value = VendaUiStatus.Ready
        _showSnackbar.value = false
    }

    private fun validateQuantity(qtnText: String, available: Int) {
        val qtn = qtnText.toIntOrNull() ?: 0
        if (qtn > available) {
            _status.value = VendaUiStatus.InsufficientStock(available)
        } else {
            _status.value = VendaUiStatus.Ready
        }
    }

    fun confirmarVenda(onSuccess: (vendaId: Long) -> Unit) {
        viewModelScope.launch {
            val state = uiState.value
            val cliente = state.selectedCliente ?: return@launch
            val reboque = state.selectedReboque ?: return@launch
            val quantidade = state.quantidade

            if (quantidade <= 0 || quantidade > state.saldoAtual) {
                _status.value = VendaUiStatus.InsufficientStock(state.saldoAtual)
                return@launch
            }

            val result = repository.registrarVenda(
                reboqueId = reboque.reboque.id,
                clienteId = cliente.id,
                quantidade = quantidade
            )

            if (result.isSuccess) {
                val vendaId = result.getOrThrow()
                _status.value = VendaUiStatus.Success(vendaId)
                onSuccess(vendaId)
            } else {
                _status.value = VendaUiStatus.GenericError("Não foi possível realizar a operação. Tente novamente.")
                _showSnackbar.value = true
            }
        }
    }

    fun simulateGenericFailure() {
        _status.value = VendaUiStatus.GenericError("Não foi possível realizar a operação. Tente novamente.")
        _showSnackbar.value = true
    }

    class Factory(private val repository: JsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NovaVendaViewModel(repository) as T
        }
    }
}

private data class Tuple5<A, B, C, D, E>(
    val first: A, val second: B, val third: C, val fourth: D, val fifth: E
)
