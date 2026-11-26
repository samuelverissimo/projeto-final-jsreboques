package br.edu.ifpe.jsreboques.ui.screens.clientes

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.jsreboques.data.local.entity.TipoCliente
import br.edu.ifpe.jsreboques.ui.components.InfoCard
import br.edu.ifpe.jsreboques.ui.components.InfoCardType
import br.edu.ifpe.jsreboques.ui.components.PrimaryButton
import br.edu.ifpe.jsreboques.ui.theme.JsBorder
import br.edu.ifpe.jsreboques.ui.theme.JsPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsSecondary
import br.edu.ifpe.jsreboques.ui.theme.JsTextPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroClienteBottomSheet(
    onDismissRequest: () -> Unit,
    onConfirm: (nome: String, tipo: TipoCliente, whatsapp: String, email: String, documento: String) -> Unit,
    errorMessage: String? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var tipo by remember { mutableStateOf(TipoCliente.FISICA) }
    var nome by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var documento by remember { mutableStateOf("") }

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
                    text = "Novo cliente",
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
                text = "Preencha os dados de contato. Campos com * são obrigatórios.",
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

            // Tipo selector chips
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = tipo == TipoCliente.FISICA,
                    onClick = {
                        tipo = TipoCliente.FISICA
                        if (documento.startsWith("CNPJ")) documento = ""
                    },
                    label = { Text("Pessoa física") },
                    leadingIcon = if (tipo == TipoCliente.FISICA) {
                        { Icon(Icons.Default.Check, contentDescription = null, tint = JsPrimary) }
                    } else null,
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = JsSecondary,
                        containerColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = tipo == TipoCliente.FISICA,
                        borderColor = JsBorder,
                        selectedBorderColor = JsSecondary
                    )
                )

                FilterChip(
                    selected = tipo == TipoCliente.JURIDICA,
                    onClick = {
                        tipo = TipoCliente.JURIDICA
                        if (documento.startsWith("CPF")) documento = ""
                    },
                    label = { Text("Pessoa jurídica") },
                    leadingIcon = if (tipo == TipoCliente.JURIDICA) {
                        { Icon(Icons.Default.Check, contentDescription = null, tint = JsPrimary) }
                    } else null,
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = JsSecondary,
                        containerColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = tipo == TipoCliente.JURIDICA,
                        borderColor = JsBorder,
                        selectedBorderColor = JsSecondary
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Nome completo *
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text(if (tipo == TipoCliente.FISICA) "Nome completo *" else "Razão social *") },
                placeholder = { Text(if (tipo == TipoCliente.FISICA) "Ex: Rafael Almeida Santos" else "Ex: Transportadora Silva") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JsPrimary,
                    unfocusedBorderColor = JsBorder
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // WhatsApp *
            OutlinedTextField(
                value = whatsapp,
                onValueChange = { whatsapp = it },
                label = { Text("WhatsApp *") },
                placeholder = { Text("(11) 90000-0404") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JsPrimary,
                    unfocusedBorderColor = JsBorder
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // E-mail
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                placeholder = { Text("rafael.santos@example.com") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JsPrimary,
                    unfocusedBorderColor = JsBorder
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // CPF / CNPJ *
            val docLabel = if (tipo == TipoCliente.FISICA) "CPF *" else "CNPJ *"
            val docPlaceholder = if (tipo == TipoCliente.FISICA) "***.***.***-04" else "**.***.***/****-04"
            OutlinedTextField(
                value = documento,
                onValueChange = { documento = it },
                label = { Text(docLabel) },
                placeholder = { Text(docPlaceholder) },
                supportingText = { Text("Documento simulado e mascarado nesta prévia") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JsPrimary,
                    unfocusedBorderColor = JsBorder
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Info Card
            InfoCard(
                title = "Dados demonstrativos",
                message = "Este contato e documento são fictícios. Não utilize estes dados para operações reais.",
                type = InfoCardType.INFO
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            PrimaryButton(
                text = "Cadastrar cliente",
                icon = Icons.Default.Check,
                onClick = {
                    onConfirm(nome, tipo, whatsapp, email, documento)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
