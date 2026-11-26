package br.edu.ifpe.jsreboques.ui.screens.clientes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.jsreboques.ui.components.ClienteCard
import br.edu.ifpe.jsreboques.ui.components.InfoCard
import br.edu.ifpe.jsreboques.ui.components.InfoCardType
import br.edu.ifpe.jsreboques.ui.components.JsBottomBar
import br.edu.ifpe.jsreboques.ui.components.JsHeader
import br.edu.ifpe.jsreboques.ui.components.SearchField
import br.edu.ifpe.jsreboques.ui.navigation.NavTarget
import br.edu.ifpe.jsreboques.ui.theme.JsBackground
import br.edu.ifpe.jsreboques.ui.theme.JsPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsSecondary
import br.edu.ifpe.jsreboques.ui.theme.JsTextPrimary

@Composable
fun ClientesScreen(
    viewModel: ClientesViewModel,
    currentRoute: String?,
    onNavigate: (NavTarget) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            JsHeader(title = "Clientes", subtitle = "Contatos e documentos")
        },
        bottomBar = {
            JsBottomBar(currentRoute = currentRoute, onNavigate = onNavigate)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openModal() },
                containerColor = JsSecondary,
                contentColor = JsPrimary,
                shape = RoundedCornerShape(24.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Novo cliente", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = JsBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchField(
                value = state.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = "Buscar nome ou documento"
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${state.filteredClientes.size} clientes cadastrados",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = JsTextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = state.filteredClientes,
                    key = { it.id }
                ) { cliente ->
                    ClienteCard(
                        cliente = cliente,
                        onClick = { /* Details */ }
                    )
                }

                item {
                    InfoCard(
                        title = "Cadastros demonstrativos",
                        message = "Nomes, contatos e documentos são simulados. CPF/CNPJ mascarados; não representam documentos reais.",
                        type = InfoCardType.INFO,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }

    if (state.isModalOpen) {
        CadastroClienteBottomSheet(
            onDismissRequest = { viewModel.closeModal() },
            onConfirm = { nome, tipo, whatsapp, email, documento ->
                viewModel.cadastrarCliente(nome, tipo, whatsapp, email, documento) {
                    viewModel.closeModal()
                }
            },
            errorMessage = state.errorMessage
        )
    }
}
