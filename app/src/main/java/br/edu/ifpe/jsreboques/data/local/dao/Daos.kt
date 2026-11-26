package br.edu.ifpe.jsreboques.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import br.edu.ifpe.jsreboques.data.local.entity.AjusteEstoqueEntity
import br.edu.ifpe.jsreboques.data.local.entity.CompraEntity
import br.edu.ifpe.jsreboques.data.local.entity.EstoqueEntity
import br.edu.ifpe.jsreboques.data.local.entity.FornecedorEntity
import br.edu.ifpe.jsreboques.data.local.entity.ResumoVendas
import br.edu.ifpe.jsreboques.data.local.entity.TipoAjuste
import br.edu.ifpe.jsreboques.data.local.entity.VendaEntity
import br.edu.ifpe.jsreboques.data.local.entity.VendaHistorico
import br.edu.ifpe.jsreboques.data.local.model.AjusteHistorico
import br.edu.ifpe.jsreboques.data.local.model.CompraHistorico
import br.edu.ifpe.jsreboques.domain.EstoqueInsuficienteException
import br.edu.ifpe.jsreboques.domain.ResultadoMovimento
import kotlinx.coroutines.flow.Flow

// ---------------------------------------------------------------- FORNECEDORES
@Dao
interface FornecedorDao {

    @Query("SELECT * FROM fornecedores ORDER BY nome COLLATE NOCASE ASC")
    fun observarTodos(): Flow<List<FornecedorEntity>>

    @Insert
    suspend fun inserir(fornecedor: FornecedorEntity): Long

    @Update
    suspend fun atualizar(fornecedor: FornecedorEntity)

    @Query("DELETE FROM fornecedores WHERE id = :id")
    suspend fun excluir(id: Long)
}

// ---------------------------------------------------------------- VENDAS / COMPRAS / ESTOQUE (F2, F3, F4, F8)
@Dao
abstract class OperacaoDao {

    @Query("SELECT quantidade FROM estoques WHERE reboqueId = :reboqueId")
    abstract suspend fun quantidadeEmEstoque(reboqueId: Long): Int?

    @Upsert
    abstract suspend fun salvarEstoque(estoque: EstoqueEntity)

    @Insert
    abstract suspend fun inserirVenda(venda: VendaEntity): Long

    @Insert
    abstract suspend fun inserirCompra(compra: CompraEntity): Long

    @Insert
    abstract suspend fun inserirAjuste(ajuste: AjusteEstoqueEntity): Long

    // ---- Operações atômicas (@Transaction): ou grava TUDO ou NADA (regra 2 do PRD)

    @Transaction
    open suspend fun registrarVenda(
        reboqueId: Long,
        clienteId: Long,
        quantidade: Int,
        valorUnitarioCents: Long
    ): ResultadoMovimento {
        require(quantidade > 0) { "Quantidade inválida" }
        val antes = quantidadeEmEstoque(reboqueId) ?: 0
        // Regra 1 do PRD: nunca vender mais do que existe em estoque.
        if (quantidade > antes) throw EstoqueInsuficienteException(antes)
        val depois = antes - quantidade
        val id = inserirVenda(
            VendaEntity(
                reboqueId = reboqueId,
                clienteId = clienteId,
                quantidade = quantidade,
                valorUnitarioCents = valorUnitarioCents,
                valorTotalCents = valorUnitarioCents * quantidade,
                saldoAntes = antes,
                saldoDepois = depois
            )
        )
        salvarEstoque(EstoqueEntity(reboqueId, depois))
        return ResultadoMovimento(id, antes, depois)
    }

    @Transaction
    open suspend fun registrarCompra(
        reboqueId: Long,
        quantidade: Int,
        valorUnitarioCents: Long
    ): ResultadoMovimento {
        require(quantidade > 0) { "Quantidade inválida" }
        val antes = quantidadeEmEstoque(reboqueId) ?: 0
        val depois = antes + quantidade
        val id = inserirCompra(
            CompraEntity(
                reboqueId = reboqueId,
                quantidade = quantidade,
                valorUnitarioCents = valorUnitarioCents,
                valorTotalCents = valorUnitarioCents * quantidade,
                saldoAntes = antes,
                saldoDepois = depois
            )
        )
        salvarEstoque(EstoqueEntity(reboqueId, depois))
        return ResultadoMovimento(id, antes, depois)
    }

    /** delta > 0 = entrada manual | delta < 0 = saída manual. */
    @Transaction
    open suspend fun registrarAjuste(
        reboqueId: Long,
        delta: Int,
        motivo: String
    ): ResultadoMovimento {
        require(delta != 0) { "Quantidade inválida" }
        val antes = quantidadeEmEstoque(reboqueId) ?: 0
        val depois = antes + delta
        if (depois < 0) throw EstoqueInsuficienteException(antes)
        val id = inserirAjuste(
            AjusteEstoqueEntity(
                reboqueId = reboqueId,
                tipo = if (delta > 0) TipoAjuste.ENTRADA else TipoAjuste.SAIDA,
                quantidade = delta,
                motivo = motivo,
                saldoAntes = antes,
                saldoDepois = depois
            )
        )
        salvarEstoque(EstoqueEntity(reboqueId, depois))
        return ResultadoMovimento(id, antes, depois)
    }

    // ---- Consultas para Painel e Histórico

    @Query(
        """
        SELECT COALESCE(SUM(valorTotalCents), 0) AS totalCents,
               COUNT(*) AS vendas,
               COALESCE(SUM(quantidade), 0) AS unidades
        FROM vendas WHERE dataHoraTimestamp >= :inicio
        """
    )
    abstract fun observarResumoVendas(inicio: Long): Flow<ResumoVendas>

    @Query(
        """
        SELECT v.id, v.quantidade, v.valorTotalCents, v.saldoAntes AS estoqueAntes, v.saldoDepois AS estoqueDepois,
               v.dataHoraTimestamp, r.nome AS reboqueNome, c.nome AS clienteNome
        FROM vendas v
        INNER JOIN reboques r ON r.id = v.reboqueId
        INNER JOIN clientes c ON c.id = v.clienteId
        ORDER BY v.dataHoraTimestamp DESC
        """
    )
    abstract fun observarVendas(): Flow<List<VendaHistorico>>

    @Query(
        """
        SELECT c.id, c.quantidade, c.valorUnitarioCents, c.valorTotalCents, c.saldoAntes AS estoqueAntes,
               c.saldoDepois AS estoqueDepois, c.dataHoraTimestamp, r.nome AS reboqueNome
        FROM compras c
        INNER JOIN reboques r ON r.id = c.reboqueId
        ORDER BY c.dataHoraTimestamp DESC
        """
    )
    abstract fun observarCompras(): Flow<List<CompraHistorico>>

    @Query(
        """
        SELECT a.id, a.quantidade, a.motivo, a.saldoAntes AS estoqueAntes, a.saldoDepois AS estoqueDepois,
               a.dataHoraTimestamp, r.nome AS reboqueNome
        FROM ajustes_estoque a
        INNER JOIN reboques r ON r.id = a.reboqueId
        ORDER BY a.dataHoraTimestamp DESC
        """
    )
    abstract fun observarAjustes(): Flow<List<AjusteHistorico>>
}
