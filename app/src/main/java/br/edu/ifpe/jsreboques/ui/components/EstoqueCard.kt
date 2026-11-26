package br.edu.ifpe.jsreboques.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.jsreboques.data.local.relation.ReboqueComEstoque
import br.edu.ifpe.jsreboques.ui.theme.JsBorder
import br.edu.ifpe.jsreboques.ui.theme.JsSurface
import br.edu.ifpe.jsreboques.ui.theme.JsTextPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextSecondary

@Composable
fun EstoqueCard(
    reboqueComEstoque: ReboqueComEstoque,
    onEntradaClick: () -> Unit,
    onSaidaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reboque = reboqueComEstoque.reboque
    val quantidade = reboqueComEstoque.quantidadeEstoque

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = JsSurface),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, JsBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reboque.nome,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = JsTextPrimary
                    )
                    Text(
                        text = "${reboque.modelo} • ${reboque.categoria}",
                        fontSize = 13.sp,
                        color = JsTextSecondary
                    )
                }

                StatusChip(quantidade = quantidade, showQuantity = false)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$quantidade ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = JsTextPrimary
                )
                Text(
                    text = "unidades",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = JsTextPrimary,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Disponível agora",
                    fontSize = 12.sp,
                    color = JsTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                SecondaryButton(
                    text = "+ Entrada",
                    onClick = onEntradaClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                SecondaryButton(
                    text = "− Saída",
                    onClick = onSaidaClick,
                    enabled = quantidade > 0,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                )
            }
        }
    }
}
