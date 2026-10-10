package br.edu.ifpe.jsreboques.ui.reboques

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import br.edu.ifpe.jsreboques.data.local.entity.ReboqueEntity
import br.edu.ifpe.jsreboques.data.local.model.ReboqueComEstoque
import br.edu.ifpe.jsreboques.data.repository.ReboqueRepository
import br.edu.ifpe.jsreboques.domain.CATEGORIAS_REBOQUE
import br.edu.ifpe.jsreboques.domain.Resultado
import br.edu.ifpe.jsreboques.domain.statusDoEstoque
import br.edu.ifpe.jsreboques.ui.components.*
import br.edu.ifpe.jsreboques.ui.theme.AzulClaro
import br.edu.ifpe.jsreboques.ui.theme.AzulPrimario
import br.edu.ifpe.jsreboques.ui.theme.AzulSuave
import br.edu.ifpe.jsreboques.ui.theme.VermelhoTexto
import br.edu.ifpe.jsreboques.util.centsParaCampo
import br.edu.ifpe.jsreboques.util.paraReais
import br.edu.ifpe.jsreboques.util.reaisParaCents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FormReboque(
    val id: Long = 0,
    val nome: String = "",
    val marca: String = "",
    val modelo: String = "",
    val categoria: String = "",
    val descricao: String = "",
    val compra: String = "",
    val venda: String = "",
    val tentouSalvar: Boolean = false
) {
    val valido: Boolean
        get() = nome.isNotBlank() && marca.isNotBlank() && modelo.isNotBlank() &&
            categoria.isNotBlank() && compra.reaisParaCents() != null && venda.reaisParaCents() != null
}

data class ReboquesUiState(
    val busca: String = "",
    val categoria: String? = null,
    val categorias: List<String> = emptyList(),
    val itens: List<ReboqueComEstoque> = emptyList(),
    val formulario: FormReboque? = null,
    val mensagem: String? = null
)

class ReboquesViewModel(private val repo: ReboqueRepository) : ViewModel() {

    private val busca = MutableStateFlow("")
    private val categoria = MutableStateFlow<String?>(null)
    private val formulario = MutableStateFlow<FormReboque?>(null)
    private val mensagem = MutableStateFlow<String?>(null)

    val estado: StateFlow<ReboquesUiState> =
        combine(repo.observarTodos(), busca, categoria, formulario, mensagem) { lista, b, c, f, m ->
            val filtrada = lista.filter { item ->
                (c == null || item.reboque.categoria == c) &&
                    (b.isBlank() || item.reboque.nome.contains(b, ignoreCase = true) ||
                        item.reboque.modelo.contains(b, ignoreCase = true))
            }
            ReboquesUiState(
                busca = b, categoria = c,
                categorias = (CATEGORIAS_REBOQUE + lista.map { it.reboque.categoria }).distinct(),
                itens = filtrada, formulario = f, mensagem = m
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReboquesUiState())

    fun aoBuscar(texto: String) { busca.value = texto }
    fun aoSelecionarCategoria(c: String?) { categoria.value = c }
    fun novo() { formulario.value = FormReboque() }
    fun alterar(f: FormReboque) { formulario.value = f }
    fun fechar() { formulario.value = null }
    fun mensagemConsumida() { mensagem.value = null }

    fun editar(item: ReboqueComEstoque) {
        val r = item.reboque
        formulario.value = FormReboque(
            id = r.id, nome = r.nome, marca = r.marca, modelo = r.modelo,
            categoria = r.categoria, descricao = r.descricao,
            compra = r.valorCompraCents.centsParaCampo(),
            venda = r.valorVendaCents.centsParaCampo()
        )
    }

    fun salvar() {
        val f = formulario.value ?: return
        if (!f.valido) {
            formulario.value = f.copy(tentouSalvar = true)
            return
        }
        val entidade = ReboqueEntity(
            id = f.id, nome = f.nome.trim(), marca = f.marca.trim(), modelo = f.modelo.trim(),
            categoria = f.categoria, descricao = f.descricao,
            valorCompraCents = f.compra.reaisParaCents() ?: return,
            valorVendaCents = f.venda.reaisParaCents() ?: return
        )
        viewModelScope.launch {
            when (val r = repo.salvar(entidade)) {
                is Resultado.Sucesso -> formulario.value = null
                is Resultado.Falha -> mensagem.value = r.mensagem
            }
        }
    }

    fun excluir() {
        val f = formulario.value ?: return
        viewModelScope.launch {
            when (val r = repo.excluir(f.id)) {
                is Resultado.Sucesso -> formulario.value = null
                is Resultado.Falha -> mensagem.value = r.mensagem
            }
        }
    }
}

@Composable
fun ReboquesScreen(
    vm: ReboquesViewModel = appViewModel<ReboquesViewModel> { ReboquesViewModel(it.reboqueRepository) }
) {
    val e by vm.estado.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { CabecalhoTela("Reboques", "Catálogo e preços de venda") }
            item { CampoBusca(e.busca, vm::aoBuscar, "Buscar por nome") }
            item {
                FiltroChips(
                    opcoes = listOf("Todos") + e.categorias,
                    selecionado = e.categoria ?: "Todos",
                    aoSelecionar = { vm.aoSelecionarCategoria(if (it == "Todos") null else it) }
                )
            }
            item {
                Text(
                    "${e.itens.size} reboques",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            // key = id: exigência do PRD para o LazyColumn
            items(e.itens, key = { it.reboque.id }) { item ->
                CartaoReboque(item, aoClicar = { vm.editar(item) })
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
            Text("Novo reboque", fontWeight = FontWeight.SemiBold)
        }

        e.formulario?.let { form ->
            FormularioReboque(
                form = form,
                aoMudar = vm::alterar,
                aoSalvar = vm::salvar,
                aoExcluir = vm::excluir,
                aoFechar = vm::fechar
            )
        }
        SnackbarDeMensagem(e.mensagem, vm::mensagemConsumida)
    }
}

@Composable
private fun CartaoReboque(item: ReboqueComEstoque, aoClicar: () -> Unit) {
    val r = item.reboque
    val status = statusDoEstoque(item.quantidade)
    Cartao(Modifier.fillMaxWidth(), aoClicar = aoClicar) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // TODO: trocar pelo foto do reboque quando houver upload de imagem
                Box(
                    Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)).background(AzulSuave),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Outlined.LocalShipping, contentDescription = null, tint = AzulPrimario) }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(r.nome, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "${r.marca} • ${r.modelo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(r.valorVendaCents.paraReais(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f)) { EtiquetaStatus(status, "${item.quantidade} un. • ${status.rotulo}") }
                Text("Ver detalhes", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioReboque(
    form: FormReboque,
    aoMudar: (FormReboque) -> Unit,
    aoSalvar: () -> Unit,
    aoExcluir: () -> Unit,
    aoFechar: () -> Unit
) {
    var confirmarExclusao by remember { mutableStateOf(false) }
    val edicao = form.id != 0L
    val t = form.tentouSalvar
    val obrigatorio = "Obrigatório"

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
                if (edicao) "Editar reboque" else "Novo reboque",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Cadastre o modelo e seus preços. Campos com * são obrigatórios.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            CampoTexto("Nome *", form.nome, { aoMudar(form.copy(nome = it)) },
                erro = if (t && form.nome.isBlank()) obrigatorio else null)
            CampoTexto("Marca *", form.marca, { aoMudar(form.copy(marca = it)) },
                erro = if (t && form.marca.isBlank()) obrigatorio else null)
            CampoTexto("Modelo *", form.modelo, { aoMudar(form.copy(modelo = it)) },
                erro = if (t && form.modelo.isBlank()) obrigatorio else null)
            CampoSelecao(
                rotulo = "Categoria *",
                textoSelecionado = form.categoria,
                opcoes = CATEGORIAS_REBOQUE,
                textoDe = { it },
                aoSelecionar = { aoMudar(form.copy(categoria = it)) },
                erro = t && form.categoria.isBlank()
            )
            CampoTexto("Preço de compra (R$) *", form.compra, { aoMudar(form.copy(compra = it)) },
                teclado = KeyboardType.Decimal,
                erro = if (t && form.compra.reaisParaCents() == null) "Informe um valor válido" else null)
            CampoTexto("Preço de venda (R$) *", form.venda, { aoMudar(form.copy(venda = it)) },
                teclado = KeyboardType.Decimal,
                erro = if (t && form.venda.reaisParaCents() == null) "Informe um valor válido" else null)

            if (!edicao) {
                CartaoAviso(
                    TipoAviso.INFO, "Estoque inicial: 0 unidades",
                    "Após cadastrar, registre uma compra ou entrada para adicionar unidades."
                )
            }
            BotaoPrimario(
                texto = if (edicao) "Salvar alterações" else "Cadastrar reboque",
                aoClicar = aoSalvar,
                icone = Icons.Filled.Check
            )
            if (edicao) {
                TextButton(onClick = { confirmarExclusao = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Excluir reboque", color = VermelhoTexto)
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }

    if (confirmarExclusao) {
        AlertDialog(
            onDismissRequest = { confirmarExclusao = false },
            title = { Text("Excluir reboque?") },
            text = { Text("Reboques com compras ou vendas registradas não podem ser excluídos.") },
            confirmButton = {
                TextButton(onClick = { confirmarExclusao = false; aoExcluir() }) {
                    Text("Excluir", color = VermelhoTexto)
                }
            },
            dismissButton = { TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar") } }
        )
    }
}
