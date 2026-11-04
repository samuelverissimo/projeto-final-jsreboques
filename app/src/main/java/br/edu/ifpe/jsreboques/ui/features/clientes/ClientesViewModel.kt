package br.edu.ifpe.jsreboques.ui.features.clientes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpe.jsreboques.model.Cliente
import br.edu.ifpe.jsreboques.util.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ClientesViewModel : ViewModel() {

    private val _estadoUi = MutableStateFlow<UiState<List<Cliente>>>(UiState.Carregando)
    val estadoUi: StateFlow<UiState<List<Cliente>>> = _estadoUi.asStateFlow()

    init {
        carregar()
    }

    fun carregar() {
        viewModelScope.launch {
            _estadoUi.value = UiState.Carregando
            try {
                // Simulando carregamento de dados
                delay(2000)
                
                // Exemplo de dados para teste (Remover ou substituir por Repository depois)
                val clientes = listOf<Cliente>(
                    // Cliente(1, "João Silva", "joao@email.com", "81999999999"),
                    // Cliente(2, "Maria Souza", "maria@email.com", "81888888888")
                )

                if (clientes.isEmpty()) {
                    _estadoUi.value = UiState.Vazio
                } else {
                    _estadoUi.value = UiState.Sucesso(clientes)
                }
            } catch (e: Exception) {
                _estadoUi.value = UiState.Erro("Erro ao carregar clientes: ${e.message}")
            }
        }
    }
}
