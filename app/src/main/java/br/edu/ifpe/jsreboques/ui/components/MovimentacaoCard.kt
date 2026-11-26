package br.edu.ifpe.jsreboques.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.jsreboques.data.local.entity.TipoAjuste
import br.edu.ifpe.jsreboques.data.local.relation.AjusteComDetalhes
import br.edu.ifpe.jsreboques.data.local.relation.CompraComDetalhes
import br.edu.ifpe.jsreboques.data.local.relation.VendaComDetalhes
import br.edu.ifpe.jsreboques.ui.theme.JsBorder
import br.edu.ifpe.jsreboques.ui.theme.JsSurface
import br.edu.ifpe.jsreboques.ui.theme.JsTextPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextSecondary
import br.edu.ifpe.jsreboques.ui.theme.StatusNormalBg
import br.edu.ifpe.jsreboques.ui.theme.StatusNormalText
import br.edu.ifpe.jsreboques.util.CurrencyFormatter
import br.edu.ifpe.jsreboques.util.DateFormatter

sealed class MovimentacaoUi {
    abstract val id: String
    abstract val timestamp: Long
    abstract val itemNome: String
    abstract val saldoAntes: Int
    abstract val saldoDepois: Int

    data class Venda(val vendaComDetalhes: VendaComDetalhes) : MovimentacaoUi() {
        override val id: String get() = "V_${vendaComDetalhes.venda.id}"
        override val timestamp: Long get() = vendaComDetalhes.venda.dataHoraTimestamp
        override val itemNome: String get() = vendaComDetalhes.reboque.nome
        override val saldoAntes: Int get() = vendaComDetalhes.venda.saldoAntes
        override val saldoDepois: Int get() = vendaComDetalhes.venda.saldoDepois
    }

    data class Compra(val compraComDetalhes: CompraComDetalhes) : MovimentacaoUi() {
        override val id: String get() = "C_${compraComDetalhes.compra.id}"
        override val timestamp: Long get() = compraComDetalhes.compra.dataHoraTimestamp
        override val itemNome: String get() = compraComDetalhes.reboque.nome
        override val saldoAntes: Int get() = compraComDetalhes.compra.saldoAntes
        override val saldoDepois: Int get() = compraComDetalhes.compra.saldoDepois
    }

    data class Ajuste(val ajusteComDetalhes: AjusteComDetalhes) : MovimentacaoUi() {
        override val id: String get() = "A_${ajusteComDetalhes.ajuste.id}"
        override val timestamp: Long get() = ajusteComDetalhes.ajuste.dataHoraTimestamp
        override val itemNome: String get() = ajusteComDetalhes.reboque.nome
        override val saldoAntes: Int get() = ajusteComDetalhes.ajuste.saldoAntes
        override val saldoDepois: Int get() = ajusteComDetalhes.ajuste.saldoDepois
    }
}

@Composable
fun MovimentacaoCard(
    movimentacao: MovimentacaoUi,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = JsSurface),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, JsBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Type & Code + Date
            val (headerTitle, subtitle, quantidadeChipText, isPositiveChip, valorText) = when (movimentacao) {
                is MovimentacaoUi.Venda -> {
                    val v = movimentacao.vendaComDetalhes
                    Tuple5(
                        "↗ Venda • ${v.venda.codigoFormatado}",
                        v.cliente.nome,
                        "−${v.venda.quantidade} un.",
                        false,
                        CurrencyFormatter.formatCents(v.venda.valorTotalCents)
                    )
                }
                is MovimentacaoUi.Compra -> {
                    val c = movimentacao.compraComDetalhes
                    Tuple5(
                        "↙ Compra • ${c.compra.codigoFormatado}",
                        "Aquisição • ${CurrencyFormatter.formatCents(c.compra.valorUnitarioCents)} / un.",
                        "+${c.compra.quantidade} un.",
                        true,
                        CurrencyFormatter.formatCents(c.compra.valorTotalCents)
                    )
                }
                is MovimentacaoUi.Ajuste -> {
                    val a = movimentacao.ajusteComDetalhes
                    val sign = if (a.ajuste.tipo == TipoAjuste.ENTRADA) "+" else "−"
                    Tuple5(
                        "⚌ Ajuste • ${a.ajuste.codigoFormatado}",
                        a.ajuste.motivo,
                        "$sign${a.ajuste.quantidade} un.",
                        a.ajuste.tipo == TipoAjuste.ENTRADA,
                        "Sem valor financeiro"
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = headerTitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = JsTextPrimary
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = DateFormatter.formatShortDate(movimentacao.timestamp),
                    fontSize = 12.sp,
                    color = JsTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Reboque Name & Subtitle
            Text(
                text = movimentacao.itemNome,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = JsTextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = JsTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quantity Chip & Value Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isPositiveChip) StatusNormalBg else Color(0xFFF1F5F9))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = quantidadeChipText,
                        color = if (isPositiveChip) StatusNormalText else JsTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = valorText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = JsTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stock transition row
            Text(
                text = "Estoque antes → depois: ${movimentacao.saldoAntes} → ${movimentacao.saldoDepois} un.",
                fontSize = 12.sp,
                color = JsTextSecondary
            )
        }
    }
}

private data class Tuple5<A, B, C, D, E>(
    val a: A, val b: B, val c: C, val d: D, val e: E
)
