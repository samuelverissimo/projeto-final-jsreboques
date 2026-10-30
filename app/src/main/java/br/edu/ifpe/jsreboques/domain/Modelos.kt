package br.edu.ifpe.jsreboques.domain

import android.util.Log
import kotlin.coroutines.cancellation.CancellationException

object Mensagens {
    // Mensagem padrão OBRIGATÓRIA do PRD para qualquer falha capturada.
    const val FALHA_PADRAO = "Não foi possível realizar a operação. Tente novamente."
    const val ESTOQUE_INSUFICIENTE = "Quantidade solicitada maior que o estoque disponível"
}

/** Normal: 3 ou mais | Baixo: 1–2 | Zero: sem saldo (igual às telas). */
const val ESTOQUE_NORMAL_MINIMO = 3

val CATEGORIAS_REBOQUE = listOf("Utilitário", "Moto", "Carga", "Plataforma")

enum class StatusEstoque(val rotulo: String) {
    NORMAL("Normal"), BAIXO("Baixo"), ZERO("Zero")
}

fun statusDoEstoque(quantidade: Int): StatusEstoque = when {
    quantidade <= 0 -> StatusEstoque.ZERO
    quantidade < ESTOQUE_NORMAL_MINIMO -> StatusEstoque.BAIXO
    else -> StatusEstoque.NORMAL
}

/** Lançada dentro da @Transaction para desfazer (rollback) uma venda inválida. */
class EstoqueInsuficienteException(val disponivel: Int) :
    Exception("Estoque insuficiente. Disponível: $disponivel")

data class ResultadoMovimento(
    val registroId: Long,
    val estoqueAntes: Int,
    val estoqueDepois: Int
)

enum class TipoMovimentacao(val rotulo: String, val prefixo: String) {
    VENDA("Venda", "V"), COMPRA("Compra", "C"), AJUSTE("Ajuste", "A")
}

fun codigoDe(tipo: TipoMovimentacao, id: Long): String =
    "${tipo.prefixo}-${id.toString().padStart(3, '0')}"

/** Item unificado do Histórico (venda, compra ou ajuste manual). */
data class Movimentacao(
    val chave: String,            // única na lista (ids de tabelas diferentes podem repetir)
    val tipo: TipoMovimentacao,
    val codigo: String,
    val reboqueNome: String,
    val detalhe: String,
    val delta: Int,               // + entrada / - saída
    val valorCents: Long?,        // null = sem valor financeiro (ajuste)
    val estoqueAntes: Int,
    val estoqueDepois: Int,
    val timestamp: Long
)

sealed interface Resultado<out T> {
    data class Sucesso<out T>(val dado: T) : Resultado<T>
    data class Falha(val mensagem: String) : Resultado<Nothing>
}

/**
 * Envolve QUALQUER operação de banco/rede em try/catch (regra 3 do PRD):
 * nunca deixa o app fechar; devolve Falha com a mensagem para o usuário.
 */
suspend fun <T> executarSeguro(bloco: suspend () -> T): Resultado<T> = try {
    Resultado.Sucesso(bloco())
} catch (e: CancellationException) {
    throw e
} catch (e: EstoqueInsuficienteException) {
    Resultado.Falha(Mensagens.ESTOQUE_INSUFICIENTE)
} catch (e: Exception) {
    Log.e("JsReboques", "Falha na operação", e)
    Resultado.Falha(Mensagens.FALHA_PADRAO)
}
