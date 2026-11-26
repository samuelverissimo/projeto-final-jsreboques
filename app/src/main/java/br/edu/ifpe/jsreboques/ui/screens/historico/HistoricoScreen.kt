package br.edu.ifpe.jsreboques.ui.screens.historico

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
import br.edu.ifpe.jsreboques.ui.components.FilterChipRow
import br.edu.ifpe.jsreboques.ui.components.FooterNote
import br.edu.ifpe.jsreboques.ui.components.JsBottomBar
import br.edu.ifpe.jsreboques.ui.components.JsTopBar
import br.edu.ifpe.jsreboques.ui.components.MovimentacaoCard
import br.edu.ifpe.jsreboques.ui.navigation.NavTarget
import br.edu.ifpe.jsreboques.ui.theme.InfoCardBg
import br.edu.ifpe.jsreboques.ui.theme.InfoCardBorder
import br.edu.ifpe.jsreboques.ui.theme.JsBackground
import br.edu.ifpe.jsreboques.ui.theme.JsPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextSecondary
import br.edu.ifpe.jsreboques.util.CurrencyFormatter

@Composable
fun HistoricoScreen(
    viewModel: HistoricoViewModel,
    currentRoute: String?,
    onNavigate: (NavTarget) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            JsTopBar(
                title = "Histórico",
                subtitle = "Compras, vendas e ajustes",
                onBackClick = onBackClick
            )
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
            // Light Blue Card
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
                            text = state.mesAnoTitle,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = JsTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${state.totalVendasCount} vendas • ${state.totalUnidadesVendidas} unidades",
                            fontSize = 13.sp,
                            color = JsTextSecondary
                        )
                    }

                    Text(
                        text = CurrencyFormatter.formatCents(state.totalVendasCents),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = JsPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            FilterChipRow(
                categories = state.filterOptions,
                selectedCategory = state.selectedFilter,
                onCategorySelected = { viewModel.onFilterSelected(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${state.filteredMovimentacoes.size} movimentações • Mais recentes primeiro",
                fontSize = 13.sp,
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
                    items = state.filteredMovimentacoes,
                    key = { it.id }
                ) { mov ->
                    MovimentacaoCard(movimentacao = mov)
                }

                item {
                    FooterNote(text = "Registros simulados • Base anterior aos novos cenários de venda e compra.")
                }
            }
        }
    }
}
