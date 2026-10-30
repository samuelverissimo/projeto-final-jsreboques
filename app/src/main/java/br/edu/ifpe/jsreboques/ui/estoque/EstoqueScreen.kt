package br.edu.ifpe.jsreboques.ui.estoque

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import br.edu.ifpe.jsreboques.data.local.model.ReboqueComEstoque
import br.edu.ifpe.jsreboques.data.repository.OperacaoRepository
import br.edu.ifpe.jsreboques.data.repository.ReboqueRepository
import br.edu.ifpe.jsreboques.domain.Resultado
import br.edu.ifpe.jsreboques.domain.StatusEstoque
import br.edu.ifpe.jsreboques.domain.statusDoEstoque
import br.edu.ifpe.jsreboques.ui.components.*
import br.edu.ifpe.jsreboques.util.somenteDigitos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AjusteForm(
    val item: ReboqueComEstoque,
    val entrada: Boolean,
    val quantidade: String = "",
    val motivo: String = ""
) {
    val qtd: Int? get() = quantidade.toIntOrNull()?.takeIf { it > 0 }
    val excede: Boolean get() = !entrada && (qtd ?: 0) > item.quantidade
    val valido: Boolean get() = qtd != null && !excede
}

data class EstoqueUiState(
    val busca: String = "",
    val filtro: StatusEstoque? = null,
    val itens: List<ReboqueComEstoque> = emptyList(),
    val total: Int = 0,
    val modelos: Int = 0,
    val precisamReposicao: Int = 0,
    val ajuste: AjusteForm? = null,
    val mensagem: String? = null
)

class EstoqueViewModel(
    reboques: ReboqueRepository,
    private val operacoes: OperacaoRepository
) : ViewModel() {

    private val busca = MutableStateFlow("")
    private val filtro = MutableStateFlow<StatusEstoque?>(null)
    private val ajuste = MutableStateFlow<AjusteForm?>(null)
    private val mensagem = MutableStateFlow<String?>(null)

    val estado: StateFlow<EstoqueUiState> =
        combine(reboques.observarTodos(), busca, filtro, ajuste, mensagem) { lista, b, f, a, m ->
            EstoqueUiState(
                busca = b, filtro = f,
                itens = lista.filter {
                    (f == null || statusDoEstoque(it.quantidade) == f) &&
                        (b.isBlank() || it.reboque.nome.contains(b, ignoreCase = true) ||
                            it.reboque.modelo.contains(b, ignoreCase = true))
                },
                total = lista.sumOf { it.quantidade },
                modelos = lista.size,
                precisamReposicao = lista.count { statusDoEstoque(it.quantidade) != StatusEstoque.NORMAL },
                ajuste = a, mensagem = m
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EstoqueUiState())

    fun aoBuscar(t: String) { busca.value = t }
    fun aoFiltrar(f: StatusEstoque?) { filtro.value = f }
    fun abrirAjuste(item: ReboqueComEstoque, entrada: Boolean) { ajuste.value = AjusteForm(item, entrada) }
    fun alterarAjuste(a: AjusteForm) { ajuste.value = a.copy(quantidade = a.quantidade.somenteDigitos().take(4)) }
    fun fecharAjuste() { ajuste.value = null }
    fun mensagemConsumida() { mensagem.value = null }

    fun confirmarAjuste() {
        val f = ajuste.value ?: return
        val q = f.qtd ?: return
        if (!f.valido) return
        val delta = if (f.entrada) q else -q
        val motivo = f.motivo.trim().ifBlank { if (f.entrada) "Entrada manual" else "Saída manual" }
        viewModelScope.launch {
            when (val r = operacoes.registrarAjuste(f.item.reboque.id, delta, motivo)) {
                is Resultado.Sucesso -> ajuste.value = null
                is Resultado.Falha -> mensagem.value = r.mensagem
            }
        }
    }
}

@Composable
fun EstoqueScreen(
    vm: EstoqueViewModel = appViewModel<EstoqueViewModel> {
        EstoqueViewModel(it.reboqueRepository, it.operacaoRepository)
    }
) {
    val e by vm.estado.collectAsStateWithLifecycle()
    val filtros = listOf("Todos") + StatusEstoque.entries.map { it.rotulo }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { CabecalhoTela("Estoque", "Saldos e movimentações") }
            item {
                CartaoSuave {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Total disponível")
                            Text(
                                "${e.modelos} modelos • ${e.precisamReposicao} precisam de reposição",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text("${e.total} un.", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
            item { CampoBusca(e.busca, vm::aoBuscar, "Buscar reboque no estoque") }
            item {
                FiltroChips(
                    opcoes = filtros,
                    selecionado = e.filtro?.rotulo ?: "Todos",
                    aoSelecionar = { escolhido -> vm.aoFiltrar(StatusEstoque.entries.firstOrNull { it.rotulo == escolhido }) }
                )
            }
            item {
                Text(
                    "Normal: $ESTOQUE_REGRA • Baixo: 1–2 • Zero: sem saldo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(e.itens, key = { it.reboque.id }) { item ->
                val status = statusDoEstoque(item.quantidade)
                Cartao(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Column(Modifier.weight(1f)) {
                                Text(item.reboque.nome, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    "${item.reboque.modelo} • ${item.reboque.categoria}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            EtiquetaStatus(status)
                        }
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("${item.quantidade}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Text(" unidades", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            Text("Disponível agora", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(onClick = { vm.abrirAjuste(item, true) }, modifier = Modifier.weight(1f)) {
                                Text("+ Entrada")
                            }
                            OutlinedButton(
                                onClick = { vm.abrirAjuste(item, false) },
                                enabled = item.quantidade > 0,
                                modifier = Modifier.weight(1f)
                            ) { Text("− Saída") }
                        }
                    }
                }
            }
            item {
                CartaoAviso(
                    TipoAviso.INFO, "Movimentações rastreáveis",
                    "Compras e vendas atualizam o saldo. Ajustes manuais ficam registrados no histórico."
                )
            }
        }
        SnackbarDeMensagem(e.mensagem, vm::mensagemConsumida)
    }

    e.ajuste?.let { a ->
        AlertDialog(
            onDismissRequest = vm::fecharAjuste,
            title = { Text(if (a.entrada) "Entrada manual" else "Saída manual") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${a.item.reboque.nome} • saldo atual: ${a.item.quantidade} un.")
                    CampoTexto(
                        "Quantidade *", a.quantidade, { vm.alterarAjuste(a.copy(quantidade = it)) },
                        teclado = KeyboardType.Number,
                        erro = if (a.excede) "Máximo disponível: ${a.item.quantidade} unidades" else null
                    )
                    CampoTexto(
                        "Motivo (opcional)", a.motivo, { vm.alterarAjuste(a.copy(motivo = it)) }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = vm::confirmarAjuste, enabled = a.valido) { Text("Confirmar") }
            },
            dismissButton = { TextButton(onClick = vm::fecharAjuste) { Text("Cancelar") } }
        )
    }
}

private const val ESTOQUE_REGRA = "3 ou mais"
