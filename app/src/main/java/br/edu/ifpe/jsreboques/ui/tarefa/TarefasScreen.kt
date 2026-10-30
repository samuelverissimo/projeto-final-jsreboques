package br.edu.ifpe.jsreboques.ui.tarefa

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import br.edu.ifpe.jsreboques.data.local.entity.TarefaEntity
import br.edu.ifpe.jsreboques.data.repository.TarefaRepository
import br.edu.ifpe.jsreboques.domain.Resultado
import br.edu.ifpe.jsreboques.ui.components.*
import br.edu.ifpe.jsreboques.ui.theme.AzulClaro
import br.edu.ifpe.jsreboques.ui.theme.AzulPrimario
import br.edu.ifpe.jsreboques.ui.theme.AzulSuave
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FormTarefa(
    val id: Int = 0,
    val titulo: String = "",
    val descricao: String = "",
    val concluida: Boolean = false,
    val tentouSalvar: Boolean = false
) {
    val valido get() = titulo.isNotBlank()
}

data class TarefasUiState(
    val busca: String = "",
    val tarefas: List<TarefaEntity> = emptyList(),
    val formulario: FormTarefa? = null,
    val mensagem: String? = null
)

class TarefaViewModel(private val repository: TarefaRepository) : ViewModel() {

    private val busca = MutableStateFlow("")
    private val formulario = MutableStateFlow<FormTarefa?>(null)
    private val mensagem = MutableStateFlow<String?>(null)

    val estado: StateFlow<TarefasUiState> =
        combine(repository.observarTodas(), busca, formulario, mensagem) { lista, b, f, m ->
            TarefasUiState(
                busca = b,
                tarefas = lista.filter {
                    b.isBlank() || it.titulo.contains(b, ignoreCase = true) ||
                            it.descricao.contains(b, ignoreCase = true)
                },
                formulario = f,
                mensagem = m
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TarefasUiState())

    fun aoBuscar(t: String) { busca.value = t }
    fun nova() { formulario.value = FormTarefa() }
    fun fecharFormulario() { formulario.value = null }
    fun mensagemConsumida() { mensagem.value = null }

    fun alterarFormulario(f: FormTarefa) {
        formulario.value = f
    }

    fun editar(t: TarefaEntity) {
        formulario.value = FormTarefa(
            id = t.id,
            titulo = t.titulo,
            descricao = t.descricao,
            concluida = t.concluida
        )
    }

    fun alternarConcluida(t: TarefaEntity) {
        viewModelScope.launch {
            val alterada = t.copy(concluida = !t.concluida)
            when (val r = repository.salvar(alterada)) {
                is Resultado.Sucesso -> {}
                is Resultado.Falha -> mensagem.value = r.mensagem
            }
        }
    }

    fun salvar() {
        val f = formulario.value ?: return
        if (!f.valido) {
            formulario.value = f.copy(tentouSalvar = true)
            return
        }
        val entidade = TarefaEntity(
            id = f.id,
            titulo = f.titulo.trim(),
            descricao = f.descricao.trim(),
            concluida = f.concluida
        )
        viewModelScope.launch {
            when (val r = repository.salvar(entidade)) {
                is Resultado.Sucesso -> formulario.value = null
                is Resultado.Falha -> mensagem.value = r.mensagem
            }
        }
    }

    fun deletar(t: TarefaEntity) {
        viewModelScope.launch {
            when (val r = repository.deletar(t)) {
                is Resultado.Sucesso -> {
                    if (formulario.value?.id == t.id) formulario.value = null
                    mensagem.value = "Tarefa excluída"
                }
                is Resultado.Falha -> mensagem.value = r.mensagem
            }
        }
    }
}

@Composable
fun TarefasScreen(
    vm: TarefaViewModel = appViewModel<TarefaViewModel> { TarefaViewModel(it.tarefaRepository) }
) {
    val e by vm.estado.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { CabecalhoTela("Tarefas", "Gerenciamento de tarefas e entregas") }
            item { CampoBusca(e.busca, vm::aoBuscar, "Buscar por título ou descrição") }
            item {
                Text(
                    "${e.tarefas.size} tarefas cadastradas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(e.tarefas, key = { it.id }) { t ->
                Cartao(
                    modifier = Modifier.fillMaxWidth(),
                    aoClicar = { vm.editar(t) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { vm.alternarConcluida(t) }) {
                            Icon(
                                imageVector = if (t.concluida) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                contentDescription = if (t.concluida) "Concluída" else "Pendente",
                                tint = if (t.concluida) AzulPrimario else Color.Gray
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = t.titulo,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textDecoration = if (t.concluida) TextDecoration.LineThrough else TextDecoration.None
                            )
                            if (t.descricao.isNotBlank()) {
                                Text(
                                    text = t.descricao,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(onClick = { vm.deletar(t) }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Deletar tarefa",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = vm::nova,
            containerColor = AzulClaro,
            contentColor = AzulPrimario,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Nova tarefa", fontWeight = FontWeight.SemiBold)
        }

        e.formulario?.let { f ->
            FormularioTarefa(
                form = f,
                aoMudar = vm::alterarFormulario,
                aoSalvar = vm::salvar,
                aoFechar = vm::fecharFormulario
            )
        }
        SnackbarDeMensagem(e.mensagem, vm::mensagemConsumida)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioTarefa(
    form: FormTarefa,
    aoMudar: (FormTarefa) -> Unit,
    aoSalvar: () -> Unit,
    aoFechar: () -> Unit
) {
    val edicao = form.id != 0
    val t = form.tentouSalvar

    ModalBottomSheet(
        onDismissRequest = aoFechar,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .imePadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                if (edicao) "Editar tarefa" else "Nova tarefa",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            CampoTexto(
                rotulo = "Título *",
                valor = form.titulo,
                aoMudar = { aoMudar(form.copy(titulo = it)) },
                erro = if (t && form.titulo.isBlank()) "Título é obrigatório" else null
            )
            CampoTexto(
                rotulo = "Descrição",
                valor = form.descricao,
                aoMudar = { aoMudar(form.copy(descricao = it)) }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { aoMudar(form.copy(concluida = !form.concluida)) }
                    .padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = form.concluida,
                    onCheckedChange = { aoMudar(form.copy(concluida = it)) }
                )
                Spacer(Modifier.width(8.dp))
                Text("Marcar como concluída", style = MaterialTheme.typography.bodyLarge)
            }
            BotaoPrimario(
                texto = if (edicao) "Salvar alterações" else "Cadastrar tarefa",
                aoClicar = aoSalvar,
                icone = Icons.Filled.Check
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}
