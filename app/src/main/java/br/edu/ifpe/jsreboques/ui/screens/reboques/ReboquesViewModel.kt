package br.edu.ifpe.jsreboques.ui.screens.reboques

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.edu.ifpe.jsreboques.data.local.entity.ReboqueEntity
import br.edu.ifpe.jsreboques.data.local.relation.ReboqueComEstoque
import br.edu.ifpe.jsreboques.data.repository.JsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ReboquesUiState(
    val reboques: List<ReboqueComEstoque> = emptyList(),
    val filteredReboques: List<ReboqueComEstoque> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "Todos",
    val categories: List<String> = listOf("Todos", "Utilitário", "Moto", "Carga", "Plataforma"),
    val isModalOpen: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

class ReboquesViewModel(private val repository: JsRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("Todos")
    private val _isModalOpen = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ReboquesUiState> = combine(
        repository.reboquesComEstoque,
        _searchQuery,
        _selectedCategory,
        _isModalOpen,
        _errorMessage
    ) { list, query, category, isModal, error ->
        val filtered = list.filter { item ->
            val matchesQuery = item.reboque.nome.contains(query, ignoreCase = true) ||
                    item.reboque.modelo.contains(query, ignoreCase = true)
            val matchesCategory = if (category == "Todos") true else item.reboque.categoria.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }

        ReboquesUiState(
            reboques = list,
            filteredReboques = filtered,
            searchQuery = query,
            selectedCategory = category,
            isModalOpen = isModal,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReboquesUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun openModal() {
        _errorMessage.value = null
        _isModalOpen.value = true
    }

    fun closeModal() {
        _isModalOpen.value = false
    }

    fun cadastrarReboque(
        nome: String,
        marca: String,
        modelo: String,
        categoria: String,
        valorCompraCents: Long,
        valorVendaCents: Long,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (nome.isBlank() || marca.isBlank() || modelo.isBlank() || categoria.isBlank() || valorVendaCents <= 0) {
                    _errorMessage.value = "Preencha todos os campos obrigatórios."
                    return@launch
                }

                val novoReboque = ReboqueEntity(
                    nome = nome,
                    marca = marca,
                    modelo = modelo,
                    categoria = categoria,
                    valorCompraCents = valorCompraCents,
                    valorVendaCents = valorVendaCents
                )

                repository.cadastrarReboque(novoReboque)
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
            return ReboquesViewModel(repository) as T
        }
    }
}
