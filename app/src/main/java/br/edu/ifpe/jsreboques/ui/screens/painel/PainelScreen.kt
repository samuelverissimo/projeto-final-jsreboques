package br.edu.ifpe.jsreboques.ui.screens.painel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AllInbox
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.jsreboques.ui.components.FooterNote
import br.edu.ifpe.jsreboques.ui.components.JsBottomBar
import br.edu.ifpe.jsreboques.ui.components.JsHeader
import br.edu.ifpe.jsreboques.ui.components.StatusChip
import br.edu.ifpe.jsreboques.ui.navigation.NavTarget
import br.edu.ifpe.jsreboques.ui.theme.JsBackground
import br.edu.ifpe.jsreboques.ui.theme.JsBorder
import br.edu.ifpe.jsreboques.ui.theme.JsPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsSurface
import br.edu.ifpe.jsreboques.ui.theme.JsTextPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextSecondary
import br.edu.ifpe.jsreboques.ui.theme.WarningCardBg
import br.edu.ifpe.jsreboques.ui.theme.WarningCardText
import br.edu.ifpe.jsreboques.util.CurrencyFormatter

@Composable
fun PainelScreen(
    viewModel: PainelViewModel,
    currentRoute: String?,
    onNavigate: (NavTarget) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            JsHeader(title = "JS Reboques", subtitle = "Gestão de fábrica e loja")
        },
        bottomBar = {
            JsBottomBar(currentRoute = currentRoute, onNavigate = onNavigate)
        },
        containerColor = JsBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Greeting
                Text(
                    text = state.greeting,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = JsTextPrimary
                )
                Text(
                    text = state.currentDateFormatted,
                    fontSize = 14.sp,
                    color = JsTextSecondary
                )
            }

            // Dark Blue Card - Vendas do Mês
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = JsPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Vendas do mês",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = state.monthYearUpper,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = CurrencyFormatter.formatCents(state.totalVendasMesCents),
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.TrendingUp,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${state.quantidadeVendasMes} vendas realizadas • ${state.quantidadeUnidadesVendidasMes} unidades vendidas",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Two summary cards side by side
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Total em Estoque
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = JsSurface),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, JsBorder, RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.AllInbox,
                                contentDescription = null,
                                tint = JsTextPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${state.totalEmEstoque}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = JsTextPrimary
                            )
                            Text(
                                text = "Total em estoque",
                                fontSize = 13.sp,
                                color = JsTextSecondary
                            )
                            Text(
                                text = "${state.totalModelosCadastrados} modelos cadastrados",
                                fontSize = 12.sp,
                                color = JsTextSecondary
                            )
                        }
                    }

                    // Alerta Estoque Baixo
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = WarningCardBg),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.Warning,
                                contentDescription = null,
                                tint = WarningCardText,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${state.totalAlertaEstoqueBaixo}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarningCardText
                            )
                            Text(
                                text = "Alerta de estoque baixo",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = WarningCardText
                            )
                            Text(
                                text = "${state.quantidadeBaixo} baixo • ${state.quantidadeZerado} zerado",
                                fontSize = 12.sp,
                                color = WarningCardText.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Section "Acesso rápido"
            item {
                Text(
                    text = "Acesso rápido",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = JsTextPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickAccessButton(
                            icon = Icons.Outlined.LocalShipping,
                            label = "Reboques",
                            onClick = { onNavigate(NavTarget.REBOQUES) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickAccessButton(
                            icon = Icons.Outlined.AllInbox,
                            label = "Estoque",
                            onClick = { onNavigate(NavTarget.ESTOQUE) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickAccessButton(
                            icon = Icons.Outlined.ShoppingCart,
                            label = "Nova venda",
                            onClick = { onNavigate(NavTarget.NOVA_VENDA) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickAccessButton(
                            icon = Icons.Outlined.PersonAdd,
                            label = "Nova compra",
                            onClick = { onNavigate(NavTarget.NOVA_COMPRA) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickAccessButton(
                            icon = Icons.Outlined.People,
                            label = "Clientes",
                            onClick = { onNavigate(NavTarget.CLIENTES) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickAccessButton(
                            icon = Icons.Outlined.History,
                            label = "Histórico",
                            onClick = { onNavigate(NavTarget.HISTORICO) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Section "Precisa de atenção"
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Precisa de atenção",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = JsTextPrimary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Ver estoque",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = JsPrimary,
                        modifier = Modifier.clickable { onNavigate(NavTarget.ESTOQUE) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = JsSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, JsBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (state.reboquesAtencao.isEmpty()) {
                            Text(
                                text = "Nenhum reboque em alerta no momento.",
                                fontSize = 14.sp,
                                color = JsTextSecondary
                            )
                        } else {
                            state.reboquesAtencao.forEachIndexed { index, item ->
                                if (index > 0) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.reboque.nome,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = JsTextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatusChip(quantidade = item.quantidadeEstoque)
                                }
                            }
                        }
                    }
                }
            }

            item {
                FooterNote()
            }
        }
    }
}

@Composable
private fun QuickAccessButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(JsSurface)
            .border(1.dp, JsBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = JsPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = JsTextPrimary
            )
        }
    }
}
