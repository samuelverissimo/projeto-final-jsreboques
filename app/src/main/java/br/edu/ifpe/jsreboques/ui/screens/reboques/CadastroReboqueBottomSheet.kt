package br.edu.ifpe.jsreboques.ui.screens.reboques

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.jsreboques.ui.components.InfoCard
import br.edu.ifpe.jsreboques.ui.components.InfoCardType
import br.edu.ifpe.jsreboques.ui.components.MoneyField
import br.edu.ifpe.jsreboques.ui.components.PrimaryButton
import br.edu.ifpe.jsreboques.ui.theme.JsBorder
import br.edu.ifpe.jsreboques.ui.theme.JsPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroReboqueBottomSheet(
    onDismissRequest: () -> Unit,
    onConfirm: (nome: String, marca: String, modelo: String, categoria: String, valorCompraCents: Long, valorVendaCents: Long) -> Unit,
    errorMessage: String? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var nome by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("JS Reboques") }
    var modelo by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Carga") }
    var valorCompraCents by remember { mutableLongStateOf(0L) }
    var valorVendaCents by remember { mutableLongStateOf(0L) }

    var expandedCategoria by remember { mutableStateOf(false) }
    val categorias = listOf("Utilitário", "Moto", "Carga", "Plataforma")

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Novo reboque",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = JsTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Fechar",
                        tint = JsTextSecondary
                    )
                }
            }

            Text(
                text = "Cadastre o modelo e seus preços. Campos com * são obrigatórios.",
                fontSize = 13.sp,
                color = JsTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!errorMessage.isNullOrEmpty()) {
                InfoCard(
                    message = errorMessage,
                    type = InfoCardType.ERROR,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Nome *
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome *") },
                placeholder = { Text("Ex: Basculante 750 kg") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JsPrimary,
                    unfocusedBorderColor = JsBorder
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Marca *
            OutlinedTextField(
                value = marca,
                onValueChange = { marca = it },
                label = { Text("Marca *") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JsPrimary,
                    unfocusedBorderColor = JsBorder
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Modelo *
            OutlinedTextField(
                value = modelo,
                onValueChange = { modelo = it },
                label = { Text("Modelo *") },
                placeholder = { Text("Ex: BC-750") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JsPrimary,
                    unfocusedBorderColor = JsBorder
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Categoria Dropdown *
            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = { expandedCategoria = !expandedCategoria }
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
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
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }
                ) {
                    categorias.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                categoria = item
                                expandedCategoria = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Preço de compra (R$) *
            MoneyField(
                valueCents = valorCompraCents,
                onValueCentsChange = { valorCompraCents = it },
                label = "Preço de compra (R$) *"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Preço de venda (R$) *
            MoneyField(
                valueCents = valorVendaCents,
                onValueCentsChange = { valorVendaCents = it },
                label = "Preço de venda (R$) *"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Info card
            InfoCard(
                title = "Estoque inicial: 0 unidades",
                message = "Após cadastrar, registre uma compra ou entrada para adicionar unidades.",
                type = InfoCardType.INFO
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            PrimaryButton(
                text = "Cadastrar reboque",
                icon = Icons.Default.Check,
                onClick = {
                    onConfirm(nome, marca, modelo, categoria, valorCompraCents, valorVendaCents)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
