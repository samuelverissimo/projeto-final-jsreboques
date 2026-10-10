package br.edu.ifpe.jsreboques.ui.venda

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import br.edu.ifpe.jsreboques.data.local.entity.ClienteEntity
import br.edu.ifpe.jsreboques.data.local.model.ReboqueComEstoque
import br.edu.ifpe.jsreboques.data.repository.ClienteRepository
import br.edu.ifpe.jsreboques.data.repository.OperacaoRepository
import br.edu.ifpe.jsreboques.data.repository.ReboqueRepository
import br.edu.ifpe.jsreboques.domain.Resultado
import br.edu.ifpe.jsreboques.domain.TipoMovimentacao
import br.edu.ifpe.jsreboques.domain.codigoDe
import br.edu.ifpe.jsreboques.domain.statusDoEstoque
import br.edu.ifpe.jsreboques.ui.components.*
import br.edu.ifpe.jsreboques.ui.theme.*
import br.edu.ifpe.jsreboques.util.paraDataCompleta
import br.edu.ifpe.jsreboques.util.paraNumeroBr
import br.edu.ifpe.jsreboques.util.paraReais
import br.edu.ifpe.jsreboques.util.somenteDigitos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VendaConcluida(
    val codigo: String,
    val dataTimestamp: Long,
    val clienteNome: String,
    val reboqueNome: String,
    val marcaModelo: String,
    val quantidade: Int,
    val precoUnitCents: Long,
    val totalCents: Long,
    val antes: Int,
    val depois: Int,
    val totalGeralAntes: Int,
    val totalGeralDepois: Int
)

data class NovaVendaUiState(
    val clientes: List<ClienteEntity> = emptyList(),
    val reboques: List<ReboqueComEstoque> = emptyList(),
    val clienteId: Long? = null,
    val reboqueId: Long? = null,
    val quantidadeTexto: String = "",
    val salvando: Boolean = false,
    val falhou: Boolean = false,
    val mensagem: String? = null,
    val concluida: VendaConcluida? = null
) {
    val cliente: ClienteEntity? get() = clientes.firstOrNull { it.id == clienteId }
    val reboque: ReboqueComEstoque? get() = reboques.firstOrNull { it.reboque.id == reboqueId }
    val quantidade: Int? get() = quantidadeTexto.toIntOrNull()?.takeIf { it > 0 }
    val disponivel: Int get() = reboque?.quantidade ?: 0
    val excede: Boolean
        get() {
            val q = quantidade ?: return false
            return reboque != null && q > disponivel
        }
    val totalCents: Long get() = (reboque?.reboque?.valorVendaCents ?: 0L) * (quantidade ?: 0)
    val podeConfirmar: Boolean
        get() = cliente != null && reboque != null && quantidade != null && !excede && !salvando
}

class NovaVendaViewModel(
    reboques: ReboqueRepository,
    clientes: ClienteRepository,
    private val operacoes: OperacaoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(NovaVendaUiState())
    val estado: StateFlow<NovaVendaUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            combine(clientes.observarTodos(), reboques.observarTodos()) { c, r -> c to r }
                .collect { (c, r) -> _estado.update { it.copy(clientes = c, reboques = r) } }
        }
    }

    fun selecionarCliente(id: Long) = _estado.update { it.copy(clienteId = id, falhou = false) }
    fun selecionarReboque(id: Long) = _estado.update { it.copy(reboqueId = id, falhou = false) }
    fun alterarQuantidade(t: String) =
        _estado.update { it.copy(quantidadeTexto = t.somenteDigitos().take(4), falhou = false) }

    fun corrigirQuantidade() = _estado.update {
        it.copy(quantidadeTexto = if (it.disponivel > 0) it.disponivel.toString() else "", falhou = false)
    }

    fun mensagemConsumida() = _estado.update { it.copy(mensagem = null) }

    /** Volta para um formulário limpo (botão "Nova venda" da tela de sucesso). */
    fun nova() = _estado.update {
        it.copy(clienteId = null, reboqueId = null, quantidadeTexto = "", falhou = false, concluida = null)
    }

    fun confirmar() {
        val e = _estado.value
        if (!e.podeConfirmar) return
        val cliente = e.cliente ?: return
        val reboque = e.reboque ?: return
        val qtd = e.quantidade ?: return
        _estado.update { it.copy(salvando = true) }

        viewModelScope.launch {
            val r = operacoes.registrarVenda(
                reboque.reboque.id, cliente.id, qtd, reboque.reboque.valorVendaCents
            )
            when (r) {
                is Resultado.Sucesso -> {
                    val totalAntes = e.reboques.sumOf { it.quantidade }
                    _estado.update {
                        it.copy(
                            salvando = false,
                            falhou = false,
                            concluida = VendaConcluida(
                                codigo = codigoDe(TipoMovimentacao.VENDA, r.dado.registroId),
                                dataTimestamp = System.currentTimeMillis(),
                                clienteNome = cliente.nome,
                                reboqueNome = reboque.reboque.nome,
                                marcaModelo = "${reboque.reboque.marca} / ${reboque.reboque.modelo}",
                                quantidade = qtd,
                                precoUnitCents = reboque.reboque.valorVendaCents,
                                totalCents = reboque.reboque.valorVendaCents * qtd,
                                antes = r.dado.estoqueAntes,
                                depois = r.dado.estoqueDepois,
                                totalGeralAntes = totalAntes,
                                totalGeralDepois = totalAntes - qtd
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
fun NovaVendaScreen(
    aoVoltar: () -> Unit,
    aoVerEstoque: () -> Unit,
    vm: NovaVendaViewModel = appViewModel<NovaVendaViewModel> {
        NovaVendaViewModel(it.reboqueRepository, it.clienteRepository, it.operacaoRepository)
    }
) {
    val e by vm.estado.collectAsStateWithLifecycle()
    val concluida = e.concluida

    Box(Modifier.fillMaxSize()) {
        if (concluida != null) {
            VendaConcluidaConteudo(concluida, aoVoltar, aoVerEstoque, aoNova = vm::nova)
        } else {
            FormularioVenda(e, vm, aoVoltar)
        }
        SnackbarDeMensagem(e.mensagem, vm::mensagemConsumida)
    }
}

@Composable
private fun FormularioVenda(e: NovaVendaUiState, vm: NovaVendaViewModel, aoVoltar: () -> Unit) {
    val reboque = e.reboque
    val qtd = e.quantidade
    val bloqueada = e.excede

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        CabecalhoTela("Nova venda", "Saída de estoque", aoVoltar)
        Column(
            Modifier.padding(horizontal = 20.dp).padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Registrar venda", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                "Selecione o cliente e confira o saldo antes de confirmar.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            CampoSelecao(
                rotulo = "Cliente *",
                textoSelecionado = e.cliente?.nome ?: "",
                opcoes = e.clientes,
                textoDe = { it.nome },
                aoSelecionar = { vm.selecionarCliente(it.id) }
            )
            CampoSelecao(
                rotulo = "Reboque *",
                textoSelecionado = reboque?.let { "${it.reboque.nome} • ${it.reboque.modelo}" } ?: "",
                opcoes = e.reboques,
                textoDe = { "${it.reboque.nome} • ${it.reboque.modelo}" },
                aoSelecionar = { vm.selecionarReboque(it.reboque.id) }
            )
            if (reboque != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${reboque.reboque.valorVendaCents.paraReais()} / un.",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    EtiquetaStatus(statusDoEstoque(reboque.quantidade), "${reboque.quantidade} un. disponíveis")
                }
            }
            CampoTexto(
                rotulo = "Quantidade *",
                valor = e.quantidadeTexto,
                aoMudar = vm::alterarQuantidade,
                teclado = KeyboardType.Number,
                erro = if (bloqueada) "Máximo disponível: ${e.disponivel} unidades" else null,
                ajuda = "Informe a quantidade de unidades vendidas"
            )

            if (reboque != null && qtd != null) {
                CartaoSuave {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Resumo da venda", fontWeight = FontWeight.Bold)
                        LinhaValor("$qtd un. × ${reboque.reboque.valorVendaCents.paraReais()}", e.totalCents.paraReais())
                        HorizontalDivider()
                        LinhaValor("Valor total (R$)", e.totalCents.paraNumeroBr(), destaque = true)
                        val apos = if (bloqueada || e.falhou) reboque.quantidade else reboque.quantidade - qtd
                        LinhaValor(
                            if (bloqueada || e.falhou) "Estoque preservado" else "Saldo após confirmar",
                            "${reboque.quantidade} un. → $apos un."
                        )
                    }
                }
            }

            when {
                bloqueada -> CartaoAviso(
                    TipoAviso.ERRO, "Venda bloqueada",
                    "Quantidade solicitada maior que o estoque disponível"
                )
                e.falhou -> CartaoAviso(
                    TipoAviso.ALERTA, "Venda não registrada",
                    "Os dados foram mantidos. O estoque continua em ${e.disponivel} unidades e nenhum valor foi registrado."
                )
                e.podeConfirmar -> CartaoAviso(
                    TipoAviso.INFO, "Pronto para confirmar",
                    "Esta é uma prévia. O estoque só será abatido após a confirmação da venda."
                )
            }

            BotaoPrimario(
                texto = if (e.falhou) "Tentar novamente" else "Confirmar venda",
                aoClicar = vm::confirmar,
                habilitado = e.podeConfirmar,
                icone = Icons.Filled.Check
            )
            if (bloqueada) BotaoSecundario("Corrigir quantidade", vm::corrigirQuantidade)
        }
    }
}

@Composable
private fun VendaConcluidaConteudo(
    v: VendaConcluida,
    aoVoltar: () -> Unit,
    aoVerEstoque: () -> Unit,
    aoNova: () -> Unit
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        CabecalhoTela("Venda concluída", "Registro ${v.codigo} • ${v.dataTimestamp.paraDataCompleta()}", aoVoltar)
        Column(
            Modifier.padding(horizontal = 20.dp).padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                Modifier.size(72.dp).clip(CircleShape).background(VerdeFundo),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Outlined.CheckCircle, null, tint = VerdeTexto, modifier = Modifier.size(36.dp)) }
            Text("Venda registrada!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                "${v.quantidade} unidades vendidas para\n${v.clienteNome}.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Cartao(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(v.reboqueNome, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    LinhaValor("Marca / Modelo", v.marcaModelo)
                    LinhaValor("Quantidade", "${v.quantidade} unidades")
                    LinhaValor("Preço unitário", v.precoUnitCents.paraReais())
                    HorizontalDivider()
                    LinhaValor("Valor total", v.totalCents.paraReais(), destaque = true)
                }
            }

            CartaoSuave {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Estoque atualizado", fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Antes da venda", style = MaterialTheme.typography.bodySmall)
                                Text("${v.antes} un.", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text("→")
                        Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = AzulClaro)) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Após a venda", style = MaterialTheme.typography.bodySmall)
                                Text("${v.depois} un.", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    LinhaValor("Total geral em estoque", "${v.totalGeralAntes} → ${v.totalGeralDepois} un.")
                }
            }

            CartaoAviso(
                TipoAviso.SUCESSO, "Histórico atualizado",
                "${v.codigo} registrada. O saldo já considera a saída confirmada."
            )
            BotaoPrimario("Ver estoque atualizado", aoVerEstoque, icone = Icons.Outlined.Inventory2)
            BotaoSecundario("Nova venda", aoNova)
        }
    }
}
