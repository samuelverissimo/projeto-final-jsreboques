package br.edu.ifpe.jsreboques.ui.screens.compra

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AllInbox
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.jsreboques.ui.components.FooterNote
import br.edu.ifpe.jsreboques.ui.components.InfoCard
import br.edu.ifpe.jsreboques.ui.components.InfoCardType
import br.edu.ifpe.jsreboques.ui.components.JsBottomBar
import br.edu.ifpe.jsreboques.ui.components.JsTopBar
import br.edu.ifpe.jsreboques.ui.components.PrimaryButton
import br.edu.ifpe.jsreboques.ui.components.SecondaryButton
import br.edu.ifpe.jsreboques.ui.components.getStatusType
import br.edu.ifpe.jsreboques.ui.navigation.NavTarget
import br.edu.ifpe.jsreboques.ui.theme.InfoCardBg
import br.edu.ifpe.jsreboques.ui.theme.InfoCardBorder
import br.edu.ifpe.jsreboques.ui.theme.JsBackground
import br.edu.ifpe.jsreboques.ui.theme.JsBorder
import br.edu.ifpe.jsreboques.ui.theme.JsPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsSurface
import br.edu.ifpe.jsreboques.ui.theme.JsTextPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextSecondary
import br.edu.ifpe.jsreboques.ui.theme.StatusBaixoBg
import br.edu.ifpe.jsreboques.ui.theme.StatusBaixoText
import br.edu.ifpe.jsreboques.ui.theme.StatusNormalBg
import br.edu.ifpe.jsreboques.ui.theme.StatusNormalText
import br.edu.ifpe.jsreboques.ui.theme.StatusZeroBg
import br.edu.ifpe.jsreboques.ui.theme.StatusZeroText
import br.edu.ifpe.jsreboques.util.CurrencyFormatter
import br.edu.ifpe.jsreboques.util.DateFormatter

@Composable
fun CompraConfirmadaScreen(
    viewModel: CompraConfirmadaViewModel,
    currentRoute: String?,
    onNavigate: (NavTarget) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val c = state.compraComDetalhes

    val codigoFormatado = c?.compra?.codigoFormatado ?: "C-002"
    val timestamp = c?.compra?.dataHoraTimestamp ?: System.currentTimeMillis()
    val dateText = DateFormatter.formatShortDayAndMonth(timestamp)

    val reboqueNome = c?.reboque?.nome ?: "Moto Duplo"
    val reboqueModelo = "${c?.reboque?.marca ?: "JS Reboques"} / ${c?.reboque?.modelo ?: "MT-02"}"
    val quantidade = c?.compra?.quantidade ?: 3
    val valorUnitarioCents = c?.compra?.valorUnitarioCents ?: 440000L
    val valorTotalCents = c?.compra?.valorTotalCents ?: 1320000L

    val saldoAntes = c?.compra?.saldoAntes ?: 2
    val saldoDepois = c?.compra?.saldoDepois ?: 5

    val statusAntesType = getStatusType(saldoAntes)
    val statusDepoisType = getStatusType(saldoDepois)

    val (bgAntes, textAntes, labelAntes) = when (statusAntesType) {
        br.edu.ifpe.jsreboques.ui.components.StatusEstoqueType.NORMAL -> Triple(StatusNormalBg, StatusNormalText, "Normal")
        br.edu.ifpe.jsreboques.ui.components.StatusEstoqueType.BAIXO -> Triple(StatusBaixoBg, StatusBaixoText, "Baixo")
        br.edu.ifpe.jsreboques.ui.components.StatusEstoqueType.ZERO -> Triple(StatusZeroBg, StatusZeroText, "Zero")
    }

    val (bgDepois, textDepois, labelDepois) = when (statusDepoisType) {
        br.edu.ifpe.jsreboques.ui.components.StatusEstoqueType.NORMAL -> Triple(StatusNormalBg, StatusNormalText, "Normal")
        br.edu.ifpe.jsreboques.ui.components.StatusEstoqueType.BAIXO -> Triple(StatusBaixoBg, StatusBaixoText, "Baixo")
        br.edu.ifpe.jsreboques.ui.components.StatusEstoqueType.ZERO -> Triple(StatusZeroBg, StatusZeroText, "Zero")
    }

    Scaffold(
        topBar = {
            JsTopBar(
                title = "Compra confirmada",
                subtitle = "Registro $codigoFormatado • $dateText",
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Check Circle Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(StatusNormalBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = StatusNormalText,
                    modifier = Modifier.size(36.dp)
                )
            }

            Text(
                text = "Compra registrada!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = JsTextPrimary
            )
            Text(
                text = "$quantidade unidades de $reboqueNome\nforam adicionadas ao estoque.",
                fontSize = 14.sp,
                color = JsTextSecondary,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Summary card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = JsSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, JsBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = reboqueNome,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = JsTextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Marca / Modelo", fontSize = 13.sp, color = JsTextSecondary)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(reboqueModelo, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = JsTextPrimary)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Quantidade comprada", fontSize = 13.sp, color = JsTextSecondary)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("$quantidade unidades", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = JsTextPrimary)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Custo unitário", fontSize = 13.sp, color = JsTextSecondary)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(CurrencyFormatter.formatCents(valorUnitarioCents), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = JsTextPrimary)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(JsBorder))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Custo total", fontSize = 14.sp, color = JsTextSecondary)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            CurrencyFormatter.formatCents(valorTotalCents),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = JsPrimary
                        )
                    }
                }
            }

            // Card "Estoque atualizado"
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = InfoCardBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, InfoCardBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Estoque atualizado", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = JsPrimary)
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(StatusNormalBg)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("+$quantidade un.", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StatusNormalText)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Antes
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgAntes)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("Antes • $labelAntes", fontSize = 11.sp, color = textAntes)
                                Text("$saldoAntes un.", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textAntes)
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = JsTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )

                        // Após
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgDepois)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("Após • $labelDepois", fontSize = 11.sp, color = textDepois)
                                Text("$saldoDepois un.", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textDepois)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Total geral em estoque", fontSize = 12.sp, color = JsTextSecondary)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${state.totalEstoqueGeral - quantidade} → ${state.totalEstoqueGeral} un.", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = JsTextPrimary)
                    }
                }
            }

            // Green Card: Reposição concluída
            InfoCard(
                title = "Reposição concluída",
                message = "$codigoFormatado • $reboqueNome saiu do alerta de estoque baixo. A entrada de +$quantidade unidades consta no histórico.",
                type = InfoCardType.SUCCESS
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Navigation buttons
            PrimaryButton(
                text = "Ver estoque atualizado",
                icon = Icons.Outlined.AllInbox,
                onClick = { onNavigate(NavTarget.ESTOQUE) }
            )

            SecondaryButton(
                text = "Nova compra",
                onClick = { onNavigate(NavTarget.NOVA_COMPRA) }
            )

            FooterNote(text = "Cenário de compra simulado • Independente da venda")
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
