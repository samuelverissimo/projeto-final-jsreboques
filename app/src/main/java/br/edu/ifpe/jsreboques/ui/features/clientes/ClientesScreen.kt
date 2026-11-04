package br.edu.ifpe.jsreboques.ui.features.clientes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.ifpe.jsreboques.model.Cliente
import br.edu.ifpe.jsreboques.ui.components.MensagemCentral
import br.edu.ifpe.jsreboques.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientesScreen(
    viewModel: ClientesViewModel = viewModel()
) {
    val estadoUi by viewModel.estadoUi.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Clientes") })
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val estado = estadoUi) {
                is UiState.Carregando -> {
                    MensagemCentral(mensagem = "Carregando clientes...", isLoading = true)
                }
                is UiState.Vazio -> {
                    MensagemCentral(mensagem = "Nenhum cliente encontrado.")
                }
                is UiState.Erro -> {
                    MensagemCentral(mensagem = estado.mensagem)
                }
                is UiState.Sucesso -> {
                    ListaClientes(clientes = estado.dados)
                }
            }
        }
    }
}

@Composable
fun ListaClientes(clientes: List<Cliente>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(clientes) { cliente ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = cliente.nome, style = MaterialTheme.typography.titleMedium)
                    Text(text = cliente.email, style = MaterialTheme.typography.bodyMedium)
                    Text(text = cliente.whatsapp, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
