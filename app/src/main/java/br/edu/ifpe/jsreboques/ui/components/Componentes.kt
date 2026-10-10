package br.edu.ifpe.jsreboques.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.edu.ifpe.jsreboques.AppContainer
import br.edu.ifpe.jsreboques.JsReboquesApp
import br.edu.ifpe.jsreboques.domain.StatusEstoque
import br.edu.ifpe.jsreboques.ui.theme.*

/** Cria o ViewModel entregando o AppContainer (repositórios) para ele. */
@Composable
inline fun <reified VM : ViewModel> appViewModel(crossinline criar: (AppContainer) -> VM): VM {
    val container = (LocalContext.current.applicationContext as JsReboquesApp).container
    return viewModel<VM>(factory = viewModelFactory { initializer { criar(container) } })
}

// ------------------------------------------------------------------ cabeçalho
@Composable
fun CabecalhoTela(
    titulo: String,
    subtitulo: String? = null,
    aoVoltar: (() -> Unit)? = null
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (aoVoltar != null) {
            IconButton(onClick = aoVoltar) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
            }
        } else {
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(AzulPrimario),
                contentAlignment = Alignment.Center
            ) { Text("JS", color = Color.White, fontWeight = FontWeight.Bold) }
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            if (subtitulo != null) {
                Text(
                    subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ------------------------------------------------------------------ cartões
@Composable
fun Cartao(
    modifier: Modifier = Modifier,
    aoClicar: (() -> Unit)? = null,
    conteudo: @Composable ColumnScope.() -> Unit
) {
    val cores = CardDefaults.cardColors(containerColor = Color.White)
    val borda = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    val forma = RoundedCornerShape(16.dp)
    if (aoClicar != null) {
        Card(onClick = aoClicar, modifier = modifier, shape = forma, colors = cores, border = borda, content = conteudo)
    } else {
        Card(modifier = modifier, shape = forma, colors = cores, border = borda, content = conteudo)
    }
}

/** Cartão azul-claro (resumos). */
@Composable
fun CartaoSuave(modifier: Modifier = Modifier, conteudo: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AzulSuave),
        content = conteudo
    )
}

enum class TipoAviso { INFO, SUCESSO, ALERTA, ERRO }

@Composable
fun CartaoAviso(tipo: TipoAviso, titulo: String, texto: String, modifier: Modifier = Modifier) {
    val (fundo, cor, icone) = when (tipo) {
        TipoAviso.INFO -> Triple(AzulSuave, AzulPrimario, Icons.Outlined.Info)
        TipoAviso.SUCESSO -> Triple(VerdeFundo, VerdeTexto, Icons.Outlined.CheckCircle)
        TipoAviso.ALERTA -> Triple(AmareloFundo, AmareloTexto, Icons.Outlined.Warning)
        TipoAviso.ERRO -> Triple(VermelhoFundo, VermelhoTexto, Icons.Outlined.Warning)
    }
    Row(
        modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(fundo).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icone, contentDescription = null, tint = cor)
        Column {
            Text(titulo, color = cor, fontWeight = FontWeight.Bold)
            Text(texto, color = cor, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// ------------------------------------------------------------------ status
fun StatusEstoque.cores(): Pair<Color, Color> = when (this) {
    StatusEstoque.NORMAL -> VerdeFundo to VerdeTexto
    StatusEstoque.BAIXO -> AmareloFundo to AmareloTexto
    StatusEstoque.ZERO -> VermelhoFundo to VermelhoTexto
}

@Composable
fun EtiquetaStatus(status: StatusEstoque, texto: String = status.rotulo) {
    val (fundo, cor) = status.cores()
    Row(
        Modifier.clip(CircleShape).background(fundo).padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(6.dp).background(cor, CircleShape))
        Spacer(Modifier.width(6.dp))
        Text(texto, color = cor, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
    }
}

// ------------------------------------------------------------------ campos
@Composable
fun CampoTexto(
    rotulo: String,
    valor: String,
    aoMudar: (String) -> Unit,
    modifier: Modifier = Modifier,
    teclado: KeyboardType = KeyboardType.Text,
    erro: String? = null,
    ajuda: String? = null
) {
    val apoio = erro ?: ajuda
    OutlinedTextField(
        value = valor,
        onValueChange = aoMudar,
        label = { Text(rotulo) },
        isError = erro != null,
        supportingText = if (apoio != null) {
            { Text(apoio) }
        } else null,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    )
}

/** Dropdown simples (sem APIs experimentais). */
@Composable
fun <T> CampoSelecao(
    rotulo: String,
    textoSelecionado: String,
    opcoes: List<T>,
    textoDe: (T) -> String,
    aoSelecionar: (T) -> Unit,
    modifier: Modifier = Modifier,
    erro: Boolean = false
) {
    var aberto by remember { mutableStateOf(false) }
    Box(modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = textoSelecionado,
            onValueChange = {},
            readOnly = true,
            label = { Text(rotulo) },
            trailingIcon = { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null) },
            isError = erro,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        // Camada transparente que captura o toque e abre o menu
        Box(Modifier.matchParentSize().clickable { aberto = true })
        DropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
            opcoes.forEach { opcao ->
                DropdownMenuItem(
                    text = { Text(textoDe(opcao)) },
                    onClick = {
                        aoSelecionar(opcao)
                        aberto = false
                    }
                )
            }
        }
    }
}

@Composable
fun CampoBusca(valor: String, aoMudar: (String) -> Unit, dica: String) {
    OutlinedTextField(
        value = valor,
        onValueChange = aoMudar,
        placeholder = { Text(dica) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = AzulSuave,
            unfocusedContainerColor = AzulSuave,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = AzulPrimario
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun FiltroChips(opcoes: List<String>, selecionado: String, aoSelecionar: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(opcoes.size, key = { opcoes[it] }) { i ->
            val opcao = opcoes[i]
            val marcado = opcao == selecionado
            FilterChip(
                selected = marcado,
                onClick = { aoSelecionar(opcao) },
                label = { Text(opcao) },
                leadingIcon = if (marcado) {
                    { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else null,
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AzulClaro,
                    selectedLabelColor = AzulPrimario,
                    selectedLeadingIconColor = AzulPrimario
                )
            )
        }
    }
}

// ------------------------------------------------------------------ botões
@Composable
fun BotaoPrimario(
    texto: String,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    icone: ImageVector? = null
) {
    Button(
        onClick = aoClicar,
        enabled = habilitado,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario)
    ) {
        if (icone != null) {
            Icon(icone, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(texto, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun BotaoSecundario(texto: String, aoClicar: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = aoClicar,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(28.dp)
    ) { Text(texto, fontWeight = FontWeight.SemiBold, color = AzulPrimario) }
}

// ------------------------------------------------------------------ diversos
@Composable
fun LinhaValor(rotulo: String, valor: String, destaque: Boolean = false) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            rotulo,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            valor,
            fontWeight = FontWeight.Bold,
            style = if (destaque) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge
        )
    }
}

/** Mostra uma mensagem temporária (Snackbar) e avisa o ViewModel quando acabar. */
@Composable
fun BoxScope.SnackbarDeMensagem(mensagem: String?, aoExibir: () -> Unit) {
    val host = remember { SnackbarHostState() }
    LaunchedEffect(mensagem) {
        if (mensagem != null) {
            host.showSnackbar(mensagem, withDismissAction = true, duration = SnackbarDuration.Long)
            aoExibir()
        }
    }
    SnackbarHost(host, modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp))
}
