package br.edu.ifpe.jsreboques.ui.screens.compra

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.jsreboques.ui.components.FooterNote
import br.edu.ifpe.jsreboques.ui.components.InfoCard
import br.edu.ifpe.jsreboques.ui.components.InfoCardType
import br.edu.ifpe.jsreboques.ui.components.JsBottomBar
import br.edu.ifpe.jsreboques.ui.components.JsTopBar
import br.edu.ifpe.jsreboques.ui.components.MoneyField
import br.edu.ifpe.jsreboques.ui.components.PrimaryButton
import br.edu.ifpe.jsreboques.ui.components.StatusChip
import br.edu.ifpe.jsreboques.ui.navigation.NavTarget
import br.edu.ifpe.jsreboques.ui.theme.InfoCardBg
import br.edu.ifpe.jsreboques.ui.theme.InfoCardBorder
import br.edu.ifpe.jsreboques.ui.theme.JsBackground
import br.edu.ifpe.jsreboques.ui.theme.JsBorder
import br.edu.ifpe.jsreboques.ui.theme.JsPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextSecondary
import br.edu.ifpe.jsreboques.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaCompraScreen(
    viewModel: NovaCompraViewModel,
    currentRoute: String?,
    onNavigate: (NavTarget) -> Unit,
    onBackClick: () -> Unit,
    onCompraConfirmada: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var expandedReboque by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            JsTopBar(
                title = "Nova compra",
                subtitle = "Entrada de estoque",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            JsBottomBar(currentRoute = currentRoute, onNavigate = onNavigate)
        },
        snackbarHost = {
            if (state.showSnackbar) {
                Snackbar(
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color.White,
                    action = {
                        IconButton(onClick = { viewModel.dismissSnackbar() }) {
                            Icon(Icons.Outlined.Close, contentDescription = "Fechar", tint = Color.White)
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(state.errorMessage ?: "Não foi possível realizar a operação. Tente novamente.")
                }
            }
        },
        containerColor = JsBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Registrar compra",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = JsTextPrimary
            )
            Text(
                text = "Registre a entrada de reboques e o custo da aquisição.",
                fontSize = 13.sp,
                color = JsTextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Dropdown Reboque *
            ExposedDropdownMenuBox(
                expanded = expandedReboque,
                onExpandedChange = { expandedReboque = !expandedReboque }
            ) {
                val reboqueLabel = state.selectedReboque?.let { "${it.reboque.nome} • ${it.reboque.modelo}" } ?: "Selecione um reboque"

                OutlinedTextField(
                    value = reboqueLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Reboque *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedReboque) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JsPrimary,
                        unfocusedBorderColor = JsBorder
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedReboque,
                    onDismissRequest = { expandedReboque = false }
                ) {
                    state.reboques.forEach { item ->
                        DropdownMenuItem(
                            text = { Text("${item.reboque.nome} • ${item.reboque.modelo}") },
                            onClick = {
                                viewModel.onReboqueSelected(item)
                                expandedReboque = false
                            }
                        )
                    }
                }
            }

            // Current Stock Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estoque atual",
                    fontSize = 14.sp,
                    color = JsTextSecondary
                )
                Spacer(modifier = Modifier.weight(1f))
                StatusChip(quantidade = state.saldoAtual, showQuantity = true)
            }

            // Quantidade comprada *
            OutlinedTextField(
                value = state.quantidadeText,
                onValueChange = { viewModel.onQuantidadeChanged(it) },
                label = { Text("Quantidade comprada *") },
                supportingText = { Text("Unidades que serão adicionadas ao estoque") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JsPrimary,
                    unfocusedBorderColor = JsBorder
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Valor de custo unitário (R$) *
            MoneyField(
                valueCents = state.valorUnitarioCents,
                onValueCentsChange = { viewModel.onValorUnitarioCentsChanged(it) },
                label = "Valor de custo unitário (R$) *"
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Card "Resumo da compra"
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = InfoCardBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, InfoCardBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Resumo da compra",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = JsPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${state.quantidade} un. × ${CurrencyFormatter.formatCents(state.valorUnitarioCents)}",
                            fontSize = 13.sp,
                            color = JsTextSecondary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = CurrencyFormatter.formatCents(state.valorTotalCents),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = JsTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Custo total (R$)",
                            fontSize = 14.sp,
                            color = JsTextSecondary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = CurrencyFormatter.formatCents(state.valorTotalCents).replace("R$", "").trim(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = JsPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Saldo após confirmar",
                            fontSize = 13.sp,
                            color = JsTextSecondary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${state.saldoAtual} un. → ${state.saldoApos} un.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = JsTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Info Card
            InfoCard(
                title = "Entrada prevista: +${state.quantidade} unidades",
                message = "Esta é uma prévia. O estoque será incrementado ao confirmar a compra.",
                type = InfoCardType.INFO
            )

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryButton(
                text = "Confirmar compra",
                icon = Icons.Default.Check,
                onClick = {
                    viewModel.confirmarCompra { compraId ->
                        onCompraConfirmada(compraId)
                    }
                }
            )

            FooterNote(text = "Dados simulados • Nenhum pagamento real")
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
