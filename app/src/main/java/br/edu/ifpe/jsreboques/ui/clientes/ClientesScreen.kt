package br.edu.ifpe.jsreboques.ui.clientes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import br.edu.ifpe.jsreboques.data.local.entity.ClienteEntity
import br.edu.ifpe.jsreboques.data.repository.ClienteRepository
import br.edu.ifpe.jsreboques.domain.Resultado
import br.edu.ifpe.jsreboques.ui.components.*
import br.edu.ifpe.jsreboques.ui.theme.AzulClaro
import br.edu.ifpe.jsreboques.ui.theme.AzulPrimario
import br.edu.ifpe.jsreboques.ui.theme.AzulSuave
import br.edu.ifpe.jsreboques.util.ehCnpj
import br.edu.ifpe.jsreboques.util.iniciais
import br.edu.ifpe.jsreboques.util.mascararDocumento
import br.edu.ifpe.jsreboques.util.somenteDigitos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FormCliente(
    val id: Long = 0,
    val juridica: Boolean = false,
    val nome: String = "",
    val whatsapp: String = "",
    val email: String = "",
    val documento: String = "",
    val tentouSalvar: Boolean = false
) {
    val whatsappOk get() = whatsapp.somenteDigitos().length >= 10
    val emailOk get() = email.isBlank() || email.contains("@")
    val documentoOk get() = documento.somenteDigitos().length == (if (juridica) 14 else 11)
    val valido get() = nome.isNotBlank() && whatsappOk && emailOk && documentoOk
}

data class ClientesUiState(
    val busca: String = "",
    val itens: List<ClienteEntity> = emptyList(),
    val formulario: FormCliente? = null,
    val mensagem: String? = null
)

class ClientesViewModel(private val repo: ClienteRepository) : ViewModel() {

    private val busca = MutableStateFlow("")
    private val formulario = MutableStateFlow<FormCliente?>(null)
    private val mensagem = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch { repo.sincronizar() }
    }

    val estado: StateFlow<ClientesUiState> =
        combine(repo.observarTodos(), busca, formulario, mensagem) { lista, b, f, m ->
            ClientesUiState(
                busca = b,
                itens = lista.filter {
                    b.isBlank() || it.nome.contains(b, ignoreCase = true) ||
                        it.documento.contains(b.somenteDigitos().ifBlank { "§" })
                },
                formulario = f, mensagem = m
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ClientesUiState())

    fun aoBuscar(t: String) { busca.value = t }
    fun novo() { formulario.value = FormCliente() }
    fun fechar() { formulario.value = null }
    fun mensagemConsumida() { mensagem.value = null }

    fun alterar(f: FormCliente) {
        formulario.value = f.copy(
            whatsapp = f.whatsapp.somenteDigitos().take(11),
            documento = f.documento.somenteDigitos().take(14)
        )
    }

    fun editar(c: ClienteEntity) {
        formulario.value = FormCliente(
            id = c.id, juridica = c.documento.ehCnpj(), nome = c.nome,
            whatsapp = c.whatsapp, email = c.email, documento = c.documento
        )
    }

    fun salvar() {
        val f = formulario.value ?: return
        if (!f.valido) {
            formulario.value = f.copy(tentouSalvar = true)
            return
        }
        val entidade = ClienteEntity(
            id = f.id, nome = f.nome.trim(), email = f.email.trim(),
            telefone = f.whatsapp.somenteDigitos(), documento = f.documento.somenteDigitos()
        )
        viewModelScope.launch {
            when (val r = repo.salvar(entidade)) {
                is Resultado.Sucesso -> formulario.value = null
                is Resultado.Falha -> mensagem.value = r.mensagem
            }
        }
    }
}

private fun String.whatsappFormatado(): String {
    val d = somenteDigitos()
    return when (d.length) {
        11 -> "(${d.take(2)}) ${d.substring(2, 7)}-${d.substring(7)}"
        10 -> "(${d.take(2)}) ${d.substring(2, 6)}-${d.substring(6)}"
        else -> this
    }
}

@Composable
fun ClientesScreen(
    vm: ClientesViewModel = appViewModel<ClientesViewModel> { ClientesViewModel(it.clienteRepository) }
) {
    val e by vm.estado.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { CabecalhoTela("Clientes", "Contatos e documentos") }
            item { CampoBusca(e.busca, vm::aoBuscar, "Buscar nome ou documento") }
            item {
                Text(
                    "${e.itens.size} clientes cadastrados",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(e.itens, key = { it.id }) { c ->
                val juridica = c.documento.ehCnpj()
                Cartao(Modifier.fillMaxWidth(), aoClicar = { vm.editar(c) }) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(44.dp).clip(CircleShape).background(AzulSuave),
                                contentAlignment = Alignment.Center
                            ) { Text(c.nome.iniciais(), fontWeight = FontWeight.Bold, color = AzulPrimario) }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(c.nome, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    if (juridica) "Pessoa jurídica" else "Pessoa física",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                        }
                        Text("${c.whatsapp.whatsappFormatado()} • WhatsApp")
                        if (c.email.isNotBlank()) Text(c.email)
                        Text("${if (juridica) "CNPJ" else "CPF"}: ${c.documento.mascararDocumento()}")
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = vm::novo,
            containerColor = AzulClaro,
            contentColor = AzulPrimario,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Novo cliente", fontWeight = FontWeight.SemiBold)
        }

        e.formulario?.let { f ->
            FormularioCliente(f, aoMudar = vm::alterar, aoSalvar = vm::salvar, aoFechar = vm::fechar)
        }
        SnackbarDeMensagem(e.mensagem, vm::mensagemConsumida)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioCliente(
    form: FormCliente,
    aoMudar: (FormCliente) -> Unit,
    aoSalvar: () -> Unit,
    aoFechar: () -> Unit
) {
    val edicao = form.id != 0L
    val t = form.tentouSalvar

    ModalBottomSheet(
        onDismissRequest = aoFechar,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
                .imePadding().navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                if (edicao) "Editar cliente" else "Novo cliente",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Preencha os dados de contato. Campos com * são obrigatórios.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FiltroChips(
                opcoes = listOf("Pessoa física", "Pessoa jurídica"),
                selecionado = if (form.juridica) "Pessoa jurídica" else "Pessoa física",
                aoSelecionar = { aoMudar(form.copy(juridica = it == "Pessoa jurídica", documento = "")) }
            )
            CampoTexto("Nome completo *", form.nome, { aoMudar(form.copy(nome = it)) },
                erro = if (t && form.nome.isBlank()) "Obrigatório" else null)
            CampoTexto("WhatsApp * (com DDD)", form.whatsapp, { aoMudar(form.copy(whatsapp = it)) },
                teclado = KeyboardType.Phone,
                erro = if (t && !form.whatsappOk) "Informe DDD + número" else null)
            CampoTexto("E-mail", form.email, { aoMudar(form.copy(email = it)) },
                teclado = KeyboardType.Email,
                erro = if (t && !form.emailOk) "E-mail inválido" else null)
            CampoTexto(
                if (form.juridica) "CNPJ * (14 dígitos)" else "CPF * (11 dígitos)",
                form.documento, { aoMudar(form.copy(documento = it)) },
                teclado = KeyboardType.Number,
                erro = if (t && !form.documentoOk) "Documento incompleto" else null
            )
            CartaoAviso(
                TipoAviso.INFO, "Documento protegido",
                "Na lista, CPF/CNPJ aparecem mascarados."
            )
            BotaoPrimario(
                texto = if (edicao) "Salvar alterações" else "Cadastrar cliente",
                aoClicar = aoSalvar,
                icone = Icons.Filled.Check
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}
