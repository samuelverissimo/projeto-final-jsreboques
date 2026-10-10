package br.edu.ifpe.jsreboques.ui.compra

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import br.edu.ifpe.jsreboques.data.local.model.ReboqueComEstoque
import br.edu.ifpe.jsreboques.data.repository.OperacaoRepository
import br.edu.ifpe.jsreboques.data.repository.ReboqueRepository
import br.edu.ifpe.jsreboques.domain.Resultado
import br.edu.ifpe.jsreboques.domain.StatusEstoque
import br.edu.ifpe.jsreboques.domain.TipoMovimentacao
import br.edu.ifpe.jsreboques.domain.codigoDe
import br.edu.ifpe.jsreboques.domain.statusDoEstoque
import br.edu.ifpe.jsreboques.ui.components.*
import br.edu.ifpe.jsreboques.ui.theme.*
import br.edu.ifpe.jsreboques.util.centsParaCampo
import br.edu.ifpe.jsreboques.util.paraDataCompleta
import br.edu.ifpe.jsreboques.util.paraNumeroBr
import br.edu.ifpe.jsreboques.util.paraReais
import br.edu.ifpe.jsreboques.util.reaisParaCents
import br.edu.ifpe.jsreboques.util.somenteDigitos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CompraConcluida(
    val codigo: String,
    val dataTimestamp: Long,
    val reboqueNome: String,
    val marcaModelo: String,
    val quantidade: Int,
    val custoUnitCents: Long,
    val totalCents: Long,
    val antes: Int,
    val depois: Int,
    val totalGeralAntes: Int,
    val totalGeralDepois: Int
)

data class NovaCompraUiState(
    val reboques: List<ReboqueComEstoque> = emptyList(),
    val reboqueId: Long? = null,
    val quantidadeTexto: String = "",
    val custoTexto: String = "",
    val salvando: Boolean = false,
    val falhou: Boolean = false,
    val mensagem: String? = null,
    val concluida: CompraConcluida? = null
) {
    val reboque: ReboqueComEstoque? get() = reboques.firstOrNull { it.reboque.id == reboqueId }
    val quantidade: Int? get() = quantidadeTexto.toIntOrNull()?.takeIf { it > 0 }
    val custoCents: Long? get() = custoTexto.reaisParaCents()
    val totalCents: Long get() = (custoCents ?: 0L) * (quantidade ?: 0)
    val podeConfirmar: Boolean
        get() = reboque != null && quantidade != null && custoCents != null && !salvando
}

class NovaCompraViewModel(
    private val reboques: ReboqueRepository,
    private val operacoes: OperacaoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(NovaCompraUiState())
    val estado: StateFlow<NovaCompraUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            reboques.observarTodos().collect { lista -> _estado.update { it.copy(reboques = lista) } }
        }
    }

    fun selecionarReboque(item: ReboqueComEstoque) = _estado.update {
        // Sugere o custo cadastrado no reboque (o usuário pode alterar)
        it.copy(
            reboqueId = item.reboque.id,
            custoTexto = item.reboque.valorCompraCents.centsParaCampo(),
            falhou = false
        )
    }

    fun alterarQuantidade(t: String) =
        _estado.update { it.copy(quantidadeTexto = t.somenteDigitos().take(4), falhou = false) }

    fun alterarCusto(t: String) = _estado.update { it.copy(custoTexto = t, falhou = false) }
    fun mensagemConsumida() = _estado.update { it.copy(mensagem = null) }

    fun nova() = _estado.update {
        it.copy(reboqueId = null, quantidadeTexto = "", custoTexto = "", falhou = false, concluida = null)
    }

    fun confirmar() {
        val e = _estado.value
        if (!e.podeConfirmar) return
        val reboque = e.reboque ?: return
        val qtd = e.quantidade ?: return
        val custo = e.custoCents ?: return
        _estado.update { it.copy(salvando = true) }

        viewModelScope.launch {
            val r = operacoes.registrarCompra(reboque.reboque.id, qtd, custo)
            when (r) {
                is Resultado.Sucesso -> {
                    val totalAntes = e.reboques.sumOf { it.quantidade }
                    _estado.update {
                        it.copy(
                            salvando = false,
                            falhou = false,
                            concluida = CompraConcluida(
                                codigo = codigoDe(TipoMovimentacao.COMPRA, r.dado.registroId),
                                dataTimestamp = System.currentTimeMillis(),
                                reboqueNome = reboque.reboque.nome,
                                marcaModelo = "${reboque.reboque.marca} / ${reboque.reboque.modelo}",
                                quantidade = qtd,
                                custoUnitCents = custo,
                                totalCents = custo * qtd,
                                antes = r.dado.estoqueAntes,
                                depois = r.dado.estoqueDepois,
                                totalGeralAntes = totalAntes,
                                totalGeralDepois = totalAntes + qtd
                            )
                        )
                    }
                }
                is Resultado.Falha -> _estado.update {
                    it.copy(salvando = false, falhou = true, mensagem = r.mensagem)
                }
            }
        }
    }
}

@Composable
fun NovaCompraScreen(
    aoVoltar: () -> Unit,
    aoVerEstoque: () -> Unit,
    vm: NovaCompraViewModel = appViewModel<NovaCompraViewModel> {
        NovaCompraViewModel(it.reboqueRepository, it.operacaoRepository)
    }
) {
    val e by vm.estado.collectAsStateWithLifecycle()
    val concluida = e.concluida

    Box(Modifier.fillMaxSize()) {
        if (concluida != null) {
            CompraConcluidaConteudo(concluida, aoVoltar, aoVerEstoque, aoNova = vm::nova)
        } else {
            FormularioCompra(e, vm, aoVoltar)
        }
        SnackbarDeMensagem(e.mensagem, vm::mensagemConsumida)
    }
}

@Composable
private fun FormularioCompra(e: NovaCompraUiState, vm: NovaCompraViewModel, aoVoltar: () -> Unit) {
    val reboque = e.reboque
    val qtd = e.quantidade

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        CabecalhoTela("Nova compra", "Entrada de estoque", aoVoltar)
        Column(
            Modifier.padding(horizontal = 20.dp).padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Registrar compra", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                "Registre a entrada de reboques e o custo da aquisição.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            CampoSelecao(
                rotulo = "Reboque *",
                textoSelecionado = reboque?.let { "${it.reboque.nome} • ${it.reboque.modelo}" } ?: "",
                opcoes = e.reboques,
                textoDe = { "${it.reboque.nome} • ${it.reboque.modelo}" },
                aoSelecionar = vm::selecionarReboque
            )
            if (reboque != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Estoque atual", modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    val status = statusDoEstoque(reboque.quantidade)
                    EtiquetaStatus(status, "${status.rotulo} • ${reboque.quantidade} un.")
                }
            }
            CampoTexto(
                "Quantidade comprada *", e.quantidadeTexto, vm::alterarQuantidade,
                teclado = KeyboardType.Number, ajuda = "Unidades que serão adicionadas ao estoque"
            )
            CampoTexto(
                "Valor de custo unitário (R$) *", e.custoTexto, vm::alterarCusto,
                teclado = KeyboardType.Decimal, ajuda = "Custo de aquisição por unidade"
            )

            if (reboque != null && qtd != null && e.custoCents != null) {
                CartaoSuave {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Resumo da compra", fontWeight = FontWeight.Bold)
                        LinhaValor("$qtd un. × ${(e.custoCents ?: 0L).paraReais()}", e.totalCents.paraReais())
                        HorizontalDivider()
                        LinhaValor("Custo total (R$)", e.totalCents.paraNumeroBr(), destaque = true)
                        LinhaValor("Saldo após confirmar", "${reboque.quantidade} un. → ${reboque.quantidade + qtd} un.")
                    }
                }
            }

            if (e.falhou) {
                CartaoAviso(
                    TipoAviso.ALERTA, "Compra não registrada",
                    "Os dados foram mantidos e nenhuma entrada foi feita no estoque."
                )
            } else if (e.podeConfirmar && qtd != null) {
                CartaoAviso(
                    TipoAviso.INFO, "Entrada prevista: +$qtd unidades",
                    "Esta é uma prévia. O estoque será incrementado ao confirmar a compra."
                )
            }

            BotaoPrimario(
                texto = if (e.falhou) "Tentar novamente" else "Confirmar compra",
                aoClicar = vm::confirmar,
                habilitado = e.podeConfirmar,
                icone = Icons.Filled.Check
            )
        }
    }
}

@Composable
private fun CompraConcluidaConteudo(
    c: CompraConcluida,
    aoVoltar: () -> Unit,
    aoVerEstoque: () -> Unit,
    aoNova: () -> Unit
) {
    val statusAntes = statusDoEstoque(c.antes)
    val statusDepois = statusDoEstoque(c.depois)

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        CabecalhoTela("Compra confirmada", "Registro ${c.codigo} • ${c.dataTimestamp.paraDataCompleta()}", aoVoltar)
        Column(
            Modifier.padding(horizontal = 20.dp).padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                Modifier.size(72.dp).clip(CircleShape).background(VerdeFundo),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Outlined.CheckCircle, null, tint = VerdeTexto, modifier = Modifier.size(36.dp)) }
            Text("Compra registrada!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                "${c.quantidade} unidades de ${c.reboqueNome}\nforam adicionadas ao estoque.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Cartao(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(c.reboqueNome, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    LinhaValor("Marca / Modelo", c.marcaModelo)
                    LinhaValor("Quantidade comprada", "${c.quantidade} unidades")
                    LinhaValor("Custo unitário", c.custoUnitCents.paraReais())
                    HorizontalDivider()
                    LinhaValor("Custo total", c.totalCents.paraReais(), destaque = true)
                }
            }

            CartaoSuave {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Estoque atualizado", fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CaixaSaldo("Antes • ${statusAntes.rotulo}", c.antes, statusAntes, Modifier.weight(1f))
                        Text("→")
                        CaixaSaldo("Após • ${statusDepois.rotulo}", c.depois, statusDepois, Modifier.weight(1f))
                    }
                    LinhaValor("Total geral em estoque", "${c.totalGeralAntes} → ${c.totalGeralDepois} un.")
                }
            }

            CartaoAviso(
                TipoAviso.SUCESSO, "Reposição concluída",
                "${c.codigo} • ${c.reboqueNome}. A entrada de +${c.quantidade} unidades consta no histórico."
            )
            BotaoPrimario("Ver estoque atualizado", aoVerEstoque, icone = Icons.Outlined.Inventory2)
            BotaoSecundario("Nova compra", aoNova)
        }
    }
}

@Composable
private fun CaixaSaldo(titulo: String, quantidade: Int, status: StatusEstoque, modifier: Modifier) {
    val (fundo, cor) = status.cores()
    Card(modifier, colors = CardDefaults.cardColors(containerColor = fundo)) {
        Column(Modifier.padding(12.dp)) {
            Text(titulo, style = MaterialTheme.typography.bodySmall, color = cor)
            Text("$quantidade un.", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = cor)
        }
    }
}
