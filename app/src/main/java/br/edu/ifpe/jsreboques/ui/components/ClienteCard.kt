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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.jsreboques.data.local.entity.ClienteEntity
import br.edu.ifpe.jsreboques.data.local.entity.TipoCliente
import br.edu.ifpe.jsreboques.ui.theme.JsBorder
import br.edu.ifpe.jsreboques.ui.theme.JsPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsSecondary
import br.edu.ifpe.jsreboques.ui.theme.JsSurface
import br.edu.ifpe.jsreboques.ui.theme.JsTextPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextSecondary

@Composable
fun ClienteCard(
    cliente: ClienteEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val initials = cliente.nome
        .split(" ")
        .filter { it.isNotEmpty() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")

    val tipoLabel = if (cliente.tipo == TipoCliente.FISICA) "Pessoa física" else "Pessoa jurídica"
    val docLabel = if (cliente.tipo == TipoCliente.FISICA) "CPF" else "CNPJ"

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = JsSurface),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, JsBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(JsSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = JsPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cliente.nome,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = JsTextPrimary
                    )
                    Text(
                        text = tipoLabel,
                        fontSize = 13.sp,
                        color = JsTextSecondary
                    )
                }

                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = JsTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contact Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = null,
                    tint = JsTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${cliente.whatsapp} • WhatsApp",
                    fontSize = 13.sp,
                    color = JsTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null,
                    tint = JsTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = cliente.email,
                    fontSize = 13.sp,
                    color = JsTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Badge,
                    contentDescription = null,
                    tint = JsTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$docLabel: ${cliente.documento}",
                    fontSize = 13.sp,
                    color = JsTextPrimary
                )
            }
        }
    }
}
