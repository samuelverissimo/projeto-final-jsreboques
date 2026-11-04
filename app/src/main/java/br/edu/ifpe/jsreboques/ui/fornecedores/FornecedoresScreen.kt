// O que faz: Exibe a lista de fornecedores parceiros e permite gerenciar seus contatos.
// Por que existe: Gerencia a interface de fornecedores e permite o cadastro de parceiros de peças.
// O que quebra se sumir: O aplicativo perde a visualização e cadastro de fornecedores.

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
import br.edu.ifpe.jsreboques.ui.UiState
import br.edu.ifpe.jsreboques.ui.components.*
import br.edu.ifpe.jsreboques.ui.theme.AzulClaro
import br.edu.ifpe.jsreboques.ui.theme.AzulPrimario
import br.edu.ifpe.jsreboques.ui.theme.AzulSuave
import br.edu.ifpe.jsreboques.util.iniciais
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

class FornecedoresViewModel(private val repo: FornecedorRepository) : ViewModel() {

    private val _estadoUi = MutableStateFlow<UiState<FornecedorEntity>>(UiState.Carregando)
    val estadoUi: StateFlow<UiState<FornecedorEntity>> = _estadoUi.asStateFlow()

    private val _busca = MutableStateFlow("")
    val busca: StateFlow<String> = _busca.asStateFlow()

    private val _formulario = MutableStateFlow<FormFornecedor?>(null)
    val formulario: StateFlow<FormFornecedor?> = _formulario.asStateFlow()

    private val _mensagem = MutableStateFlow<String?>(null)
    val mensagem: StateFlow<String?> = _mensagem.asStateFlow()

    init {
        carregar()
    }

    fun carregar() {
        _estadoUi.value = UiState.Carregando
        viewModelScope.launch {
            try {
                repo.observarTodos().collect { lista ->
                    val query = _busca.value
                    val filtrados = lista.filter {
                        query.isBlank() || it.nome.contains(query, ignoreCase = true) ||
                                it.contato.contains(query, ignoreCase = true) ||
                                (it.documento?.contains(query) == true)
                    }
                    if (lista.isEmpty()) {
                        _estadoUi.value = UiState.Vazio
                    } else {
                        _estadoUi.value = UiState.Sucesso(filtrados)
                    }
                }
            } catch (e: IOException) {
                _estadoUi.value = UiState.Erro("Sem conexão. Verifique a internet e tente novamente.")
            } catch (e: Exception) {
                _estadoUi.value = UiState.Erro("Não foi possível carregar os dados.")
            }
        }
    }

    fun aoBuscar(t: String) {
        _busca.value = t
        carregar()
    }

    fun novo() { _formulario.value = FormFornecedor() }
    fun fechar() { _formulario.value = null }
    fun mensagemConsumida() { _mensagem.value = null }

    fun alterar(f: FormFornecedor) {
        _formulario.value = f
    }

    fun editar(f: FornecedorEntity) {
        _formulario.value = FormFornecedor(
            id = f.id,
            nome = f.nome,
            contato = f.contato,
            documento = f.documento ?: ""
        )
    }

    fun salvar() {
        val f = _formulario.value ?: return
        if (!f.valido) {
            _formulario.value = f.copy(tentouSalvar = true)
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
                is Resultado.Sucesso -> _formulario.value = null
                is Resultado.Falha -> _mensagem.value = r.mensagem
            }
        }
    }

    fun excluir(id: Long) {
        viewModelScope.launch {
            when (val r = repo.excluir(id)) {
                is Resultado.Sucesso -> {
                    if (_formulario.value?.id == id) _formulario.value = null
                    _mensagem.value = "Fornecedor excluído"
                }
                is Resultado.Falha -> _mensagem.value = r.mensagem
            }
        }
    }
}

@Composable
fun FornecedoresScreen(
    vm: FornecedoresViewModel = appViewModel<FornecedoresViewModel> { FornecedoresViewModel(it.fornecedorRepository) }
) {
    val estadoUi by vm.estadoUi.collectAsStateWithLifecycle()
    val busca by vm.busca.collectAsStateWithLifecycle()
    val formulario by vm.formulario.collectAsStateWithLifecycle()
    val mensagem by vm.mensagem.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            CabecalhoTela("Fornecedores", "Parceiros e fornecedores de peças")
            Box(Modifier.padding(horizontal = 20.dp)) {
                CampoBusca(busca, vm::aoBuscar, "Buscar nome, contato ou documento")
            }
            Spacer(Modifier.height(12.dp))

            when (val atual = estadoUi) {
                is UiState.Carregando -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Vazio -> {
                    MensagemCentral(
                        texto = "Nenhum fornecedor cadastrado.",
                        rotuloBotao = "Cadastrar fornecedor",
                        aoClicar = vm::novo
                    )
                }
                is UiState.Erro -> {
                    MensagemCentral(
                        texto = atual.mensagem,
                        rotuloBotao = "Tentar novamente",
                        aoClicar = vm::carregar
                    )
                }
                is UiState.Sucesso -> {
                    LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                "${atual.itens.size} fornecedores cadastrados",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        items(atual.itens, key = { it.id }) { f ->
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

        formulario?.let { form ->
            FormularioFornecedor(
                form = form,
                aoMudar = vm::alterar,
                aoSalvar = vm::salvar,
                aoFechar = vm::fechar
            )
        }
        SnackbarDeMensagem(mensagem, vm::mensagemConsumida)
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
