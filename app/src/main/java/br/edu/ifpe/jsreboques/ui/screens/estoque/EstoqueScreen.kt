package br.edu.ifpe.jsreboques.ui.screens.estoque

import androidx.compose.foundation.border
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.jsreboques.data.local.entity.TipoAjuste
import br.edu.ifpe.jsreboques.ui.components.EstoqueCard
import br.edu.ifpe.jsreboques.ui.components.FilterChipRow
import br.edu.ifpe.jsreboques.ui.components.InfoCard
import br.edu.ifpe.jsreboques.ui.components.InfoCardType
import br.edu.ifpe.jsreboques.ui.components.JsBottomBar
import br.edu.ifpe.jsreboques.ui.components.JsHeader
import br.edu.ifpe.jsreboques.ui.components.SearchField
import br.edu.ifpe.jsreboques.ui.navigation.NavTarget
import br.edu.ifpe.jsreboques.ui.theme.InfoCardBg
import br.edu.ifpe.jsreboques.ui.theme.InfoCardBorder
import br.edu.ifpe.jsreboques.ui.theme.JsBackground
import br.edu.ifpe.jsreboques.ui.theme.JsPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextSecondary

@Composable
fun EstoqueScreen(
    viewModel: EstoqueViewModel,
    currentRoute: String?,
    onNavigate: (NavTarget) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            JsHeader(title = "Estoque", subtitle = "Saldos e movimentações")
        },
        bottomBar = {
            JsBottomBar(currentRoute = currentRoute, onNavigate = onNavigate)
        },
        containerColor = JsBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Light blue summary card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = InfoCardBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, InfoCardBorder, RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Total disponível",
                            fontSize = 14.sp,
                            color = JsTextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${state.totalModelos} modelos • ${state.modelosPrecisamReposicao} precisam de reposição",
                            fontSize = 13.sp,
                            color = JsTextSecondary
                        )
                    }

                    Text(
                        text = "${state.totalDisponivel} un.",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = JsPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            SearchField(
                value = state.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = "Buscar reboque no estoque"
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilterChipRow(
                categories = state.filterOptions,
                selectedCategory = state.selectedFilter,
                onCategorySelected = { viewModel.onFilterSelected(it) }
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Normal: 3 ou mais • Baixo: 1–2 • Zero: sem saldo",
                fontSize = 12.sp,
                color = JsTextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

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
                    EstoqueCard(
                        reboqueComEstoque = item,
                        onEntradaClick = { viewModel.openAjusteDialog(item, TipoAjuste.ENTRADA) },
                        onSaidaClick = { viewModel.openAjusteDialog(item, TipoAjuste.SAIDA) }
                    )
                }

                item {
                    InfoCard(
                        title = "Movimentações rastreáveis",
                        message = "Compras e vendas atualizam o saldo. Ajustes manuais ficam registrados no histórico.",
                        type = InfoCardType.INFO,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }

    if (state.isAjusteDialogOpen && state.selectedReboqueForAjuste != null) {
        AjusteEstoqueDialog(
            reboqueComEstoque = state.selectedReboqueForAjuste!!,
            tipoAjuste = state.tipoAjuste,
            onDismissRequest = { viewModel.closeAjusteDialog() },
            onConfirm = { quantidade, motivo ->
                viewModel.registrarAjuste(quantidade, motivo)
            },
            errorMessage = state.errorMessage
        )
    }
}
