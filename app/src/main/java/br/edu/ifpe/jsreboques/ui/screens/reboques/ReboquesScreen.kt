package br.edu.ifpe.jsreboques.ui.screens.reboques

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.jsreboques.ui.components.FilterChipRow
import br.edu.ifpe.jsreboques.ui.components.FooterNote
import br.edu.ifpe.jsreboques.ui.components.JsBottomBar
import br.edu.ifpe.jsreboques.ui.components.JsHeader
import br.edu.ifpe.jsreboques.ui.components.ReboqueCard
import br.edu.ifpe.jsreboques.ui.components.SearchField
import br.edu.ifpe.jsreboques.ui.navigation.NavTarget
import br.edu.ifpe.jsreboques.ui.theme.JsBackground
import br.edu.ifpe.jsreboques.ui.theme.JsPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsSecondary
import br.edu.ifpe.jsreboques.ui.theme.JsTextPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextSecondary

@Composable
fun ReboquesScreen(
    viewModel: ReboquesViewModel,
    currentRoute: String?,
    onNavigate: (NavTarget) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            JsHeader(title = "Reboques", subtitle = "Catálogo e preços de venda")
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
                text = { Text("Novo reboque", fontWeight = FontWeight.Bold) }
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
                placeholder = "Buscar por nome"
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilterChipRow(
                categories = state.categories,
                selectedCategory = state.selectedCategory,
                onCategorySelected = { viewModel.onCategorySelected(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle info row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${state.filteredReboques.size} reboques",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = JsTextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Nome A–Z",
                    fontSize = 13.sp,
                    color = JsTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = state.filteredReboques,
                    key = { it.reboque.id }
                ) { item ->
                    ReboqueCard(
                        reboqueComEstoque = item,
                        onClick = { /* Item click / details */ }
                    )
                }

                item {
                    FooterNote(text = "Preços em reais • Estoque disponível para venda")
                }
            }
        }
    }

    if (state.isModalOpen) {
        CadastroReboqueBottomSheet(
            onDismissRequest = { viewModel.closeModal() },
            onConfirm = { nome, marca, modelo, categoria, vCompra, vVenda ->
                viewModel.cadastrarReboque(nome, marca, modelo, categoria, vCompra, vVenda) {
                    viewModel.closeModal()
                }
            },
            errorMessage = state.errorMessage
        )
    }
}
