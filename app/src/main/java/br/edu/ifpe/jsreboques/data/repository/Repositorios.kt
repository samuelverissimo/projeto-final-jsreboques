package br.edu.ifpe.jsreboques.data.repository

import android.util.Log
import br.edu.ifpe.jsreboques.data.local.dao.ClienteDao
import br.edu.ifpe.jsreboques.data.local.dao.FornecedorDao
import br.edu.ifpe.jsreboques.data.local.dao.OperacaoDao
import br.edu.ifpe.jsreboques.data.local.dao.ReboqueDao
import br.edu.ifpe.jsreboques.data.local.dao.TarefaDao
import br.edu.ifpe.jsreboques.data.local.entity.ClienteEntity
import br.edu.ifpe.jsreboques.data.local.entity.FornecedorEntity
import br.edu.ifpe.jsreboques.data.local.entity.ReboqueEntity
import br.edu.ifpe.jsreboques.data.local.entity.TarefaEntity
import br.edu.ifpe.jsreboques.data.local.model.ReboqueComEstoque
import br.edu.ifpe.jsreboques.data.local.entity.ResumoVendas
import br.edu.ifpe.jsreboques.domain.Movimentacao
import br.edu.ifpe.jsreboques.domain.Resultado
import br.edu.ifpe.jsreboques.domain.ResultadoMovimento
import br.edu.ifpe.jsreboques.domain.TipoMovimentacao
import br.edu.ifpe.jsreboques.domain.codigoDe
import br.edu.ifpe.jsreboques.domain.executarSeguro
import br.edu.ifpe.jsreboques.util.inicioDoMes
import br.edu.ifpe.jsreboques.util.paraReais
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine

/** Se uma leitura do banco falhar, emite um valor padrão em vez de derrubar o app. */
private fun <T> Flow<T>.comFallback(valorPadrao: T): Flow<T> = catch {
    Log.e("JsReboques", "Falha ao ler dados", it)
    emit(valorPadrao)
}

class ReboqueRepository(private val dao: ReboqueDao) {

    fun observarTodos(): Flow<List<ReboqueComEstoque>> =
        dao.observarTodos().comFallback(emptyList())

    /** id == 0 cria (com estoque 0); id != 0 atualiza. */
    suspend fun salvar(reboque: ReboqueEntity): Resultado<Unit> = executarSeguro {
        if (reboque.id == 0L) dao.cadastrar(reboque) else dao.atualizar(reboque)
        Unit
    }

    suspend fun excluir(id: Long): Resultado<Unit> = executarSeguro {
        dao.excluir(id) // falha (FK RESTRICT) se já houver venda/compra -> mensagem padrão
    }
}

class ClienteRepository(private val dao: ClienteDao) {

    fun observarTodos(): Flow<List<ClienteEntity>> =
        dao.observarTodos().comFallback(emptyList())

    suspend fun salvar(cliente: ClienteEntity): Resultado<Unit> = executarSeguro {
        if (cliente.id == 0L) dao.inserir(cliente) else dao.atualizar(cliente)
        Unit
    }
}

class OperacaoRepository(private val dao: OperacaoDao) {

    suspend fun registrarVenda(
        reboqueId: Long, clienteId: Long, quantidade: Int, valorUnitarioCents: Long
    ): Resultado<ResultadoMovimento> = executarSeguro {
        dao.registrarVenda(reboqueId, clienteId, quantidade, valorUnitarioCents)
    }

    suspend fun registrarCompra(
        reboqueId: Long, quantidade: Int, valorUnitarioCents: Long
    ): Resultado<ResultadoMovimento> = executarSeguro {
        dao.registrarCompra(reboqueId, quantidade, valorUnitarioCents)
    }

    suspend fun registrarAjuste(
        reboqueId: Long, delta: Int, motivo: String
    ): Resultado<ResultadoMovimento> = executarSeguro {
        dao.registrarAjuste(reboqueId, delta, motivo)
    }

    fun observarResumoMes(): Flow<ResumoVendas> =
        dao.observarResumoVendas(inicioDoMes()).comFallback(ResumoVendas(0, 0, 0))

    /** Junta vendas + compras + ajustes numa lista só, do mais recente ao mais antigo. */
    fun observarHistorico(): Flow<List<Movimentacao>> =
        combine(dao.observarVendas(), dao.observarCompras(), dao.observarAjustes()) { vendas, compras, ajustes ->
            val v = vendas.map {
                Movimentacao(
                    chave = "V-${it.id}", tipo = TipoMovimentacao.VENDA,
                    codigo = codigoDe(TipoMovimentacao.VENDA, it.id),
                    reboqueNome = it.reboqueNome, detalhe = it.clienteNome,
                    delta = -it.quantidade, valorCents = it.valorTotalCents,
                    estoqueAntes = it.estoqueAntes, estoqueDepois = it.estoqueDepois,
                    timestamp = it.dataHoraTimestamp
                )
            }
            val c = compras.map {
                Movimentacao(
                    chave = "C-${it.id}", tipo = TipoMovimentacao.COMPRA,
                    codigo = codigoDe(TipoMovimentacao.COMPRA, it.id),
                    reboqueNome = it.reboqueNome,
                    detalhe = "Aquisição • ${it.valorUnitarioCents.paraReais()} / un.",
                    delta = it.quantidade, valorCents = it.valorTotalCents,
                    estoqueAntes = it.estoqueAntes, estoqueDepois = it.estoqueDepois,
                    timestamp = it.dataHoraTimestamp
                )
            }
            val a = ajustes.map {
                Movimentacao(
                    chave = "A-${it.id}", tipo = TipoMovimentacao.AJUSTE,
                    codigo = codigoDe(TipoMovimentacao.AJUSTE, it.id),
                    reboqueNome = it.reboqueNome, detalhe = it.motivo,
                    delta = it.quantidade, valorCents = null,
                    estoqueAntes = it.estoqueAntes, estoqueDepois = it.estoqueDepois,
                    timestamp = it.dataHoraTimestamp
                )
            }
            (v + c + a).sortedByDescending { it.timestamp }
        }.comFallback(emptyList())
}

class TarefaRepository(private val dao: TarefaDao) {

    fun observarTodas(): Flow<List<TarefaEntity>> =
        dao.listarTodas().comFallback(emptyList())

    suspend fun buscarPorId(id: Int): TarefaEntity? = dao.buscarPorId(id)

    suspend fun salvar(tarefa: TarefaEntity): Resultado<Unit> = executarSeguro {
        if (tarefa.id == 0) dao.inserir(tarefa) else dao.atualizar(tarefa)
        Unit
    }

    suspend fun deletar(tarefa: TarefaEntity): Resultado<Unit> = executarSeguro {
        dao.deletar(tarefa)
        Unit
    }

    suspend fun deletarTodas(): Resultado<Unit> = executarSeguro {
        dao.deletarTodas()
        Unit
    }
}

class FornecedorRepository(private val dao: FornecedorDao) {

    fun observarTodos(): Flow<List<FornecedorEntity>> =
        dao.observarTodos().comFallback(emptyList())

    suspend fun salvar(fornecedor: FornecedorEntity): Resultado<Unit> = executarSeguro {
        if (fornecedor.id == 0L) dao.inserir(fornecedor) else dao.atualizar(fornecedor)
        Unit
    }

    suspend fun excluir(id: Long): Resultado<Unit> = executarSeguro {
        dao.excluir(id)
    }
}
