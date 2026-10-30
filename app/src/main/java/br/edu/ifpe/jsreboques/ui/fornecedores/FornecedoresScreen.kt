package br.edu.ifpe.jsreboques.ui.fornecedores

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
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
import br.edu.ifpe.jsreboques.data.local.entity.FornecedorEntity
import br.edu.ifpe.jsreboques.data.repository.FornecedorRepository
import br.edu.ifpe.jsreboques.domain.Resultado
import br.edu.ifpe.jsreboques.ui.components.*
import br.edu.ifpe.jsreboques.ui.theme.AzulClaro
import br.edu.ifpe.jsreboques.ui.theme.AzulPrimario
import br.edu.ifpe.jsreboques.ui.theme.AzulSuave
import br.edu.ifpe.jsreboques.util.iniciais
import br.edu.ifpe.jsreboques.util.somenteDigitos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FormFornecedor(
    val id: Long = 0,
    val nome: String = "",
    val contato: String = "",
    val documento: String = "",
    val tentouSalvar: Boolean = false
) {
    val valido get() = nome.isNotBlank() && contato.isNotBlank()
}

data class FornecedoresUiState(
    val busca: String = "",
    val itens: List<FornecedorEntity> = emptyList(),
    val formulario: FormFornecedor? = null,
    val mensagem: String? = null
)

class FornecedoresViewModel(private val repo: FornecedorRepository) : ViewModel() {

    private val busca = MutableStateFlow("")
    private val formulario = MutableStateFlow<FormFornecedor?>(null)
    private val mensagem = MutableStateFlow<String?>(null)

    val estado: StateFlow<FornecedoresUiState> =
        combine(repo.observarTodos(), busca, formulario, mensagem) { lista, b, f, m ->
            FornecedoresUiState(
                busca = b,
                itens = lista.filter {
                    b.isBlank() || it.nome.contains(b, ignoreCase = true) ||
                            it.contato.contains(b, ignoreCase = true) ||
                            (it.documento?.contains(b) == true)
                },
                formulario = f,
                mensagem = m
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FornecedoresUiState())

    fun aoBuscar(t: String) { busca.value = t }
    fun novo() { formulario.value = FormFornecedor() }
    fun fechar() { formulario.value = null }
    fun mensagemConsumida() { mensagem.value = null }

    fun alterar(f: FormFornecedor) {
        formulario.value = f
    }

    fun editar(f: FornecedorEntity) {
        formulario.value = FormFornecedor(
            id = f.id,
            nome = f.nome,
            contato = f.contato,
            documento = f.documento ?: ""
        )
    }

    fun salvar() {
        val f = formulario.value ?: return
        if (!f.valido) {
            formulario.value = f.copy(tentouSalvar = true)
            return
        }
        val entidade = FornecedorEntity(
            id = f.id,
            nome = f.nome.trim(),
            contato = f.contato.trim(),
            documento = f.documento.ifBlank { null }
        )
        viewModelScope.launch {
            when (val r = repo.salvar(entidade)) {
                is Resultado.Sucesso -> formulario.value = null
                is Resultado.Falha -> mensagem.value = r.mensagem
            }
        }
    }

    fun excluir(id: Long) {
        viewModelScope.launch {
            when (val r = repo.excluir(id)) {
                is Resultado.Sucesso -> {
                    if (formulario.value?.id == id) formulario.value = null
                    mensagem.value = "Fornecedor excluído"
                }
                is Resultado.Falha -> mensagem.value = r.mensagem
            }
        }
    }
}

@Composable
fun FornecedoresScreen(
    vm: FornecedoresViewModel = appViewModel<FornecedoresViewModel> { FornecedoresViewModel(it.fornecedorRepository) }
) {
    val e by vm.estado.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { CabecalhoTela("Fornecedores", "Parceiros e fornecedores de peças") }
            item { CampoBusca(e.busca, vm::aoBuscar, "Buscar nome, contato ou documento") }
            item {
                Text(
                    "${e.itens.size} fornecedores cadastrados",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(e.itens, key = { it.id }) { f ->
                Cartao(Modifier.fillMaxWidth(), aoClicar = { vm.editar(f) }) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(44.dp).clip(CircleShape).background(AzulSuave),
                            contentAlignment = Alignment.Center
                        ) { Text(f.nome.iniciais(), fontWeight = FontWeight.Bold, color = AzulPrimario) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(f.nome, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Contato: ${f.contato}", style = MaterialTheme.typography.bodyMedium)
                            f.documento?.let { doc ->
                                if (doc.isNotBlank()) Text("Doc: $doc", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        IconButton(onClick = { vm.excluir(f.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                        }
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
            Text("Novo fornecedor", fontWeight = FontWeight.SemiBold)
        }

        e.formulario?.let { form ->
            FormularioFornecedor(
                form = form,
                aoMudar = vm::alterar,
                aoSalvar = vm::salvar,
                aoFechar = vm::fechar
            )
        }
        SnackbarDeMensagem(e.mensagem, vm::mensagemConsumida)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioFornecedor(
    form: FormFornecedor,
    aoMudar: (FormFornecedor) -> Unit,
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
                if (edicao) "Editar fornecedor" else "Novo fornecedor",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            CampoTexto(
                rotulo = "Nome / Razão Social *",
                valor = form.nome,
                aoMudar = { aoMudar(form.copy(nome = it)) },
                erro = if (t && form.nome.isBlank()) "Campo obrigatório" else null
            )
            CampoTexto(
                rotulo = "Contato / Telefone *",
                valor = form.contato,
                aoMudar = { aoMudar(form.copy(contato = it)) },
                teclado = KeyboardType.Phone,
                erro = if (t && form.contato.isBlank()) "Campo obrigatório" else null
            )
            CampoTexto(
                rotulo = "CNPJ / Documento",
                valor = form.documento,
                aoMudar = { aoMudar(form.copy(documento = it)) },
                teclado = KeyboardType.Number
            )
            BotaoPrimario(
                texto = if (edicao) "Salvar alterações" else "Cadastrar fornecedor",
                aoClicar = aoSalvar,
                icone = Icons.Filled.Check
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}
