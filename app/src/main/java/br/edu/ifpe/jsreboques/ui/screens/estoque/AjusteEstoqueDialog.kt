package br.edu.ifpe.jsreboques.ui.screens.estoque

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.jsreboques.data.local.entity.TipoAjuste
import br.edu.ifpe.jsreboques.data.local.relation.ReboqueComEstoque
import br.edu.ifpe.jsreboques.ui.components.InfoCard
import br.edu.ifpe.jsreboques.ui.components.InfoCardType
import br.edu.ifpe.jsreboques.ui.components.PrimaryButton
import br.edu.ifpe.jsreboques.ui.theme.JsBorder
import br.edu.ifpe.jsreboques.ui.theme.JsPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextSecondary

@Composable
fun AjusteEstoqueDialog(
    reboqueComEstoque: ReboqueComEstoque,
    tipoAjuste: TipoAjuste,
    onDismissRequest: () -> Unit,
    onConfirm: (quantidade: Int, motivo: String) -> Unit,
    errorMessage: String? = null
) {
    var quantidadeText by remember { mutableStateOf("1") }
    var motivo by remember { mutableStateOf("") }

    val isEntrada = tipoAjuste == TipoAjuste.ENTRADA
    val actionTitle = if (isEntrada) "Entrada de estoque" else "Saída de estoque"

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = actionTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = JsTextPrimary
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${reboqueComEstoque.reboque.nome} (${reboqueComEstoque.reboque.modelo})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = JsPrimary
                )
                Text(
                    text = "Saldo atual: ${reboqueComEstoque.quantidadeEstoque} unidades",
                    fontSize = 13.sp,
                    color = JsTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (!errorMessage.isNullOrEmpty()) {
                    InfoCard(
                        message = errorMessage,
                        type = InfoCardType.ERROR,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Quantidade *
                OutlinedTextField(
                    value = quantidadeText,
                    onValueChange = { input -> quantidadeText = input.filter { it.isDigit() } },
                    label = { Text("Quantidade *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JsPrimary,
                        unfocusedBorderColor = JsBorder
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Motivo
                OutlinedTextField(
                    value = motivo,
                    onValueChange = { motivo = it },
                    label = { Text("Motivo / Observação") },
                    placeholder = { Text(if (isEntrada) "Ex: Ajuste físico de inventário" else "Ex: Saída manual • uso interno") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JsPrimary,
                        unfocusedBorderColor = JsBorder
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            PrimaryButton(
                text = "Confirmar",
                onClick = {
                    val qtn = quantidadeText.toIntOrNull() ?: 0
                    onConfirm(qtn, motivo)
                },
                modifier = Modifier.height(44.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar", color = JsTextSecondary)
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
