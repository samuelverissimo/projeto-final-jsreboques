package br.edu.ifpe.jsreboques.ui.screens.clientes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.edu.ifpe.jsreboques.data.local.entity.ClienteEntity
import br.edu.ifpe.jsreboques.data.local.entity.TipoCliente
import br.edu.ifpe.jsreboques.data.repository.JsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ClientesUiState(
    val clientes: List<ClienteEntity> = emptyList(),
    val filteredClientes: List<ClienteEntity> = emptyList(),
    val searchQuery: String = "",
    val isModalOpen: Boolean = false,
    val errorMessage: String? = null
)

class ClientesViewModel(private val repository: JsRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _isModalOpen = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ClientesUiState> = combine(
        repository.clientes,
        _searchQuery,
        _isModalOpen,
        _errorMessage
    ) { list, query, isModal, error ->
        val filtered = list.filter { cliente ->
            cliente.nome.contains(query, ignoreCase = true) ||
                    cliente.documento.contains(query, ignoreCase = true) ||
                    cliente.whatsapp.contains(query, ignoreCase = true)
        }

        ClientesUiState(
            clientes = list,
            filteredClientes = filtered,
            searchQuery = query,
            isModalOpen = isModal,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ClientesUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun openModal() {
        _errorMessage.value = null
        _isModalOpen.value = true
    }

    fun closeModal() {
        _isModalOpen.value = false
    }

    fun cadastrarCliente(
        nome: String,
        tipo: TipoCliente,
        whatsapp: String,
        email: String,
        documento: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (nome.isBlank() || whatsapp.isBlank() || documento.isBlank()) {
                    _errorMessage.value = "Preencha todos os campos obrigatórios."
                    return@launch
                }

                val novoCliente = ClienteEntity(
                    nome = nome,
                    tipo = tipo,
                    whatsapp = whatsapp,
                    email = email,
                    documento = documento
                )

                repository.cadastrarCliente(novoCliente)
                _isModalOpen.value = false
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Não foi possível realizar a operação. Tente novamente."
            }
        }
    }

    class Factory(private val repository: JsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ClientesViewModel(repository) as T
        }
    }
}
