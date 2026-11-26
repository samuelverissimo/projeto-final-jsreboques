package br.edu.ifpe.jsreboques.ui.screens.estoque

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.edu.ifpe.jsreboques.data.local.entity.TipoAjuste
import br.edu.ifpe.jsreboques.data.local.relation.ReboqueComEstoque
import br.edu.ifpe.jsreboques.data.repository.JsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EstoqueUiState(
    val reboques: List<ReboqueComEstoque> = emptyList(),
    val filteredReboques: List<ReboqueComEstoque> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: String = "Todos",
    val filterOptions: List<String> = listOf("Todos", "Normal", "Baixo", "Zero"),
    val totalDisponivel: Int = 0,
    val totalModelos: Int = 0,
    val modelosPrecisamReposicao: Int = 0,
    val selectedReboqueForAjuste: ReboqueComEstoque? = null,
    val tipoAjuste: TipoAjuste = TipoAjuste.ENTRADA,
    val isAjusteDialogOpen: Boolean = false,
    val errorMessage: String? = null
)

class EstoqueViewModel(private val repository: JsRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow("Todos")
    private val _selectedReboqueForAjuste = MutableStateFlow<ReboqueComEstoque?>(null)
    private val _tipoAjuste = MutableStateFlow(TipoAjuste.ENTRADA)
    private val _isAjusteDialogOpen = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    private val _filteredState = combine(
        repository.reboquesComEstoque,
        _searchQuery,
        _selectedFilter
    ) { list, query, filter ->
        val totalDisponivel = list.sumOf { it.quantidadeEstoque }
        val totalModelos = list.size
        val precisamReposicao = list.count { it.quantidadeEstoque <= 2 }

        val filtered = list.filter { item ->
            val matchesQuery = item.reboque.nome.contains(query, ignoreCase = true) ||
                    item.reboque.modelo.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "Normal" -> item.quantidadeEstoque >= 3
                "Baixo" -> item.quantidadeEstoque in 1..2
                "Zero" -> item.quantidadeEstoque == 0
                else -> true
            }

            matchesQuery && matchesFilter
        }

        Triple(list, filtered, Triple(totalDisponivel, totalModelos, precisamReposicao))
    }

    val uiState: StateFlow<EstoqueUiState> = combine(
        _filteredState,
        _searchQuery,
        _selectedFilter,
        _selectedReboqueForAjuste,
        _tipoAjuste
    ) { (list, filtered, metrics), query, filter, selectedReboque, tipoAjuste ->
        EstoqueUiState(
            reboques = list,
            filteredReboques = filtered,
            searchQuery = query,
            selectedFilter = filter,
            totalDisponivel = metrics.first,
            totalModelos = metrics.second,
            modelosPrecisamReposicao = metrics.third,
            selectedReboqueForAjuste = selectedReboque,
            tipoAjuste = tipoAjuste,
            isAjusteDialogOpen = _isAjusteDialogOpen.value,
            errorMessage = _errorMessage.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EstoqueUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelected(filter: String) {
        _selectedFilter.value = filter
    }

    fun openAjusteDialog(reboque: ReboqueComEstoque, tipo: TipoAjuste) {
        _selectedReboqueForAjuste.value = reboque
        _tipoAjuste.value = tipo
        _errorMessage.value = null
        _isAjusteDialogOpen.value = true
    }

    fun closeAjusteDialog() {
        _isAjusteDialogOpen.value = false
    }

    fun registrarAjuste(quantidade: Int, motivo: String) {
        viewModelScope.launch {
            val reboque = _selectedReboqueForAjuste.value ?: return@launch
            try {
                if (quantidade <= 0) {
                    _errorMessage.value = "Informe uma quantidade válida."
                    return@launch
                }

                val result = repository.registrarAjuste(
                    reboqueId = reboque.reboque.id,
                    tipo = _tipoAjuste.value,
                    quantidade = quantidade,
                    motivo = if (motivo.isBlank()) "Ajuste manual" else motivo
                )

                if (result.isSuccess) {
                    _isAjusteDialogOpen.value = false
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message
                        ?: "Não foi possível realizar a operação. Tente novamente."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Não foi possível realizar a operação. Tente novamente."
            }
        }
    }

    class Factory(private val repository: JsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EstoqueViewModel(repository) as T
        }
    }
}
