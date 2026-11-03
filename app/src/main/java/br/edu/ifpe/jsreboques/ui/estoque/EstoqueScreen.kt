// O que faz: Exibe os saldos e permite realizar ajustes manuais de entrada/saída no estoque.
// Por que existe: Gerencia a visualização do estoque tratando os quatro estados de tela (Carregando, Vazio, Erro e Sucesso).
// O que quebra se sumir: O controle de saldos de estoque e os ajustes manuais de inventário deixam de funcionar.

package br.edu.ifpe.jsreboques.ui.estoque

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import br.edu.ifpe.jsreboques.R
import br.edu.ifpe.jsreboques.data.local.model.ReboqueComEstoque
import br.edu.ifpe.jsreboques.data.repository.OperacaoRepository
import br.edu.ifpe.jsreboques.data.repository.ReboqueRepository
import br.edu.ifpe.jsreboques.domain.Resultado
import br.edu.ifpe.jsreboques.domain.StatusEstoque
import br.edu.ifpe.jsreboques.domain.statusDoEstoque
import br.edu.ifpe.jsreboques.ui.UiState
import br.edu.ifpe.jsreboques.ui.components.*
import br.edu.ifpe.jsreboques.ui.theme.AzulPrimario
import br.edu.ifpe.jsreboques.util.somenteDigitos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

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

class EstoqueViewModel(
    private val reboques: ReboqueRepository,
    private val operacoes: OperacaoRepository
) : ViewModel() {

    private val _estadoUi = MutableStateFlow<UiState<ReboqueComEstoque>>(UiState.Carregando)
    val estadoUi: StateFlow<UiState<ReboqueComEstoque>> = _estadoUi.asStateFlow()

    private val busca = MutableStateFlow("")
    private val filtro = MutableStateFlow<StatusEstoque?>(null)
    private val ajuste = MutableStateFlow<AjusteForm?>(null)
    private val mensagem = MutableStateFlow<String?>(null)

    val buscaAtual get() = busca.value
    val filtroAtual get() = filtro.value
    val ajusteAtual get() = ajuste.value
    val mensagemAtual get() = mensagem.value

    var totalEstoque = 0
        private set
    var modelosCount = 0
        private set
    var precisamReposicaoCount = 0
        private set

    init {
        carregar()
    }

    fun carregar() {
        viewModelScope.launch {
            _estadoUi.value = UiState.Carregando
            try {
                reboques.observarTodos().collect { lista ->
                    totalEstoque = lista.sumOf { it.quantidade }
                    modelosCount = lista.size
                    precisamReposicaoCount = lista.count { statusDoEstoque(it.quantidade) != StatusEstoque.NORMAL }

                    val b = busca.value
                    val f = filtro.value
                    val filtrados = lista.filter {
                        (f == null || statusDoEstoque(it.quantidade) == f) &&
                            (b.isBlank() || it.reboque.nome.contains(b, ignoreCase = true) ||
                                it.reboque.modelo.contains(b, ignoreCase = true))
                    }
                    _estadoUi.value = if (filtrados.isEmpty()) {
                        UiState.Vazio
                    } else {
                        UiState.Sucesso(filtrados)
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
        busca.value = t
        carregar()
    }

    fun aoFiltrar(f: StatusEstoque?) {
        filtro.value = f
        carregar()
    }

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
                is Resultado.Sucesso -> {
                    ajuste.value = null
                    carregar()
                }
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
    val estadoUi by vm.estadoUi.collectAsStateWithLifecycle()
    val filtros = listOf("Todos") + StatusEstoque.entries.map { it.rotulo }

    Box(Modifier.fillMaxSize()) {
        when (val atual = estadoUi) {
            is UiState.Carregando -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AzulPrimario)
                }
            }

            is UiState.Vazio -> {
                Column(Modifier.fillMaxSize()) {
                    CabecalhoTela("Estoque", "Saldos e movimentações")
                    MensagemCentral(
                        texto = stringResource(R.string.lista_vazia),
                        rotuloBotao = stringResource(R.string.tentar_novamente),
                        aoClicar = vm::carregar
                    )
                }
            }

            is UiState.Erro -> {
                Column(Modifier.fillMaxSize()) {
                    CabecalhoTela("Estoque", "Saldos e movimentações")
                    MensagemCentral(
                        texto = atual.mensagem,
                        rotuloBotao = stringResource(R.string.tentar_novamente),
                        aoClicar = vm::carregar
                    )
                }
            }

            is UiState.Sucesso -> {
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
                                        "${vm.modelosCount} modelos • ${vm.precisamReposicaoCount} precisam de reposição",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text("${vm.totalEstoque} un.", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    item { CampoBusca(vm.buscaAtual, vm::aoBuscar, "Buscar reboque no estoque") }
                    item {
                        FiltroChips(
                            opcoes = filtros,
                            selecionado = vm.filtroAtual?.rotulo ?: "Todos",
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
                    items(atual.itens, key = { it.reboque.id }) { item ->
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
            }
        }
        SnackbarDeMensagem(vm.mensagemAtual, vm::mensagemConsumida)
    }

    vm.ajusteAtual?.let { a ->
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
