package br.edu.ifpe.jsreboques.ui.screens.venda

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import br.edu.ifpe.jsreboques.data.local.entity.TipoCliente
import br.edu.ifpe.jsreboques.ui.components.FooterNote
import br.edu.ifpe.jsreboques.ui.components.InfoCard
import br.edu.ifpe.jsreboques.ui.components.InfoCardType
import br.edu.ifpe.jsreboques.ui.components.JsBottomBar
import br.edu.ifpe.jsreboques.ui.components.JsTopBar
import br.edu.ifpe.jsreboques.ui.components.PrimaryButton
import br.edu.ifpe.jsreboques.ui.components.SecondaryButton
import br.edu.ifpe.jsreboques.ui.components.StatusChip
import br.edu.ifpe.jsreboques.ui.navigation.NavTarget
import br.edu.ifpe.jsreboques.ui.theme.InfoCardBg
import br.edu.ifpe.jsreboques.ui.theme.InfoCardBorder
import br.edu.ifpe.jsreboques.ui.theme.JsBackground
import br.edu.ifpe.jsreboques.ui.theme.JsBorder
import br.edu.ifpe.jsreboques.ui.theme.JsPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsSurface
import br.edu.ifpe.jsreboques.ui.theme.JsTextPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextSecondary
import br.edu.ifpe.jsreboques.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaVendaScreen(
    viewModel: NovaVendaViewModel,
    currentRoute: String?,
    onNavigate: (NavTarget) -> Unit,
    onBackClick: () -> Unit,
    onVendaConcluida: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    var expandedCliente by remember { mutableStateOf(false) }
    var expandedReboque by remember { mutableStateOf(false) }

    val isInsufficient = state.status is VendaUiStatus.InsufficientStock
    val isGenericError = state.status is VendaUiStatus.GenericError

    Scaffold(
        topBar = {
            JsTopBar(
                title = "Nova venda",
                subtitle = "Saída de estoque",
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
                    Text("Não foi possível realizar a operação. Tente novamente.")
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
                text = "Registrar venda",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = JsTextPrimary
            )
            Text(
                text = "Selecione o cliente e confira o saldo antes de confirmar.",
                fontSize = 13.sp,
                color = JsTextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Dropdown Cliente *
            ExposedDropdownMenuBox(
                expanded = expandedCliente,
                onExpandedChange = { expandedCliente = !expandedCliente }
            ) {
                val clienteName = state.selectedCliente?.nome ?: "Selecione um cliente"
                val clienteTipo = if (state.selectedCliente?.tipo == TipoCliente.FISICA) "Pessoa física" else "Pessoa jurídica"

                OutlinedTextField(
                    value = clienteName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cliente *") },
                    supportingText = { Text("$clienteTipo • Cadastro demonstrativo") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCliente) },
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
                    expanded = expandedCliente,
                    onDismissRequest = { expandedCliente = false }
                ) {
                    state.clientes.forEach { cliente ->
                        DropdownMenuItem(
                            text = { Text(cliente.nome) },
                            onClick = {
                                viewModel.onClienteSelected(cliente)
                                expandedCliente = false
                            }
                        )
                    }
                }
            }

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

            // Price & Availability Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${CurrencyFormatter.formatCents(state.valorUnitarioCents)} / un.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = JsTextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                StatusChip(
                    quantidade = state.saldoAtual,
                    showQuantity = true
                )
            }

            // Quantidade *
            val isQuantityError = isInsufficient
            OutlinedTextField(
                value = state.quantidadeText,
                onValueChange = { viewModel.onQuantidadeChanged(it) },
                label = { Text("Quantidade *") },
                supportingText = {
                    if (isQuantityError) {
                        Text(
                            text = "Máximo disponível: ${state.saldoAtual} unidades",
                            color = Color(0xFFB3261E)
                        )
                    } else {
                        Text("Informe a quantidade de unidades vendidas")
                    }
                },
                isError = isQuantityError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isQuantityError) Color(0xFFB3261E) else JsPrimary,
                    unfocusedBorderColor = if (isQuantityError) Color(0xFFB3261E) else JsBorder
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Card "Resumo da venda"
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
                        text = "Resumo da venda",
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
                            text = "Valor total (R$)",
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
                            text = if (isInsufficient) "Estoque preservado" else "Saldo após confirmar",
                            fontSize = 13.sp,
                            color = JsTextSecondary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = if (isInsufficient) {
                                "${state.saldoAtual} un. → ${state.saldoAtual} un."
                            } else {
                                "${state.saldoAtual} un. → ${state.saldoApos} un."
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = JsTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // State specific cards
            when {
                isInsufficient -> {
                    InfoCard(
                        title = "Venda bloqueada",
                        message = "Quantidade solicitada maior que o estoque disponível",
                        type = InfoCardType.ERROR
                    )
                }
                isGenericError -> {
                    InfoCard(
                        title = "Venda não registrada",
                        message = "Os dados foram mantidos. O estoque continua em ${state.saldoAtual} unidades e nenhum valor foi registrado.",
                        type = InfoCardType.WARNING
                    )
                }
                else -> {
                    InfoCard(
                        title = "Pronto para confirmar",
                        message = "Esta é uma prévia. O estoque só será abatido após a confirmação da venda.",
                        type = InfoCardType.INFO
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            if (isInsufficient) {
                PrimaryButton(
                    text = "Confirmar venda",
                    onClick = { },
                    enabled = false
                )
                Spacer(modifier = Modifier.height(8.dp))
                SecondaryButton(
                    text = "Corrigir quantidade",
                    onClick = { viewModel.resetQuantity() }
                )
            } else {
                PrimaryButton(
                    text = if (isGenericError) "Tentar novamente" else "Confirmar venda",
                    icon = Icons.Default.Check,
                    onClick = {
                        viewModel.confirmarVenda { vendaId ->
                            onVendaConcluida(vendaId)
                        }
                    }
                )
            }

            FooterNote(text = "Dados simulados • Nenhuma cobrança real")
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
