package br.edu.ifpe.jsreboques.data.repository

import androidx.room.withTransaction
import br.edu.ifpe.jsreboques.data.local.AppDatabase
import br.edu.ifpe.jsreboques.data.local.entity.AjusteEstoqueEntity
import br.edu.ifpe.jsreboques.data.local.entity.ClienteEntity
import br.edu.ifpe.jsreboques.data.local.entity.CompraEntity
import br.edu.ifpe.jsreboques.data.local.entity.EstoqueEntity
import br.edu.ifpe.jsreboques.data.local.entity.ReboqueEntity
import br.edu.ifpe.jsreboques.data.local.entity.TipoAjuste
import br.edu.ifpe.jsreboques.data.local.entity.VendaEntity
import br.edu.ifpe.jsreboques.data.local.relation.AjusteComDetalhes
import br.edu.ifpe.jsreboques.data.local.relation.CompraComDetalhes
import br.edu.ifpe.jsreboques.data.local.relation.ReboqueComEstoque
import br.edu.ifpe.jsreboques.data.local.relation.VendaComDetalhes
import kotlinx.coroutines.flow.Flow

class JsRepository(private val database: AppDatabase) {

    private val reboqueDao = database.reboqueDao()
    private val estoqueDao = database.estoqueDao()
    private val clienteDao = database.clienteDao()
    private val vendaDao = database.vendaDao()
    private val compraDao = database.compraDao()
    private val ajusteDao = database.ajusteEstoqueDao()

    val reboquesComEstoque: Flow<List<ReboqueComEstoque>> = reboqueDao.getReboquesComEstoque()
    val clientes: Flow<List<ClienteEntity>> = clienteDao.getAllClientes()
    val vendasComDetalhes: Flow<List<VendaComDetalhes>> = vendaDao.getAllVendasComDetalhes()
    val comprasComDetalhes: Flow<List<CompraComDetalhes>> = compraDao.getAllComprasComDetalhes()
    val ajustesComDetalhes: Flow<List<AjusteComDetalhes>> = ajusteDao.getAllAjustesComDetalhes()

    suspend fun getReboqueComEstoqueById(id: Long): ReboqueComEstoque? {
        return reboqueDao.getReboqueComEstoqueById(id)
    }

    suspend fun getVendaComDetalhesById(id: Long): VendaComDetalhes? {
        return vendaDao.getVendaComDetalhesById(id)
    }

    suspend fun getCompraComDetalhesById(id: Long): CompraComDetalhes? {
        return compraDao.getCompraComDetalhesById(id)
    }

    suspend fun cadastrarReboque(reboque: ReboqueEntity): Long {
        return database.withTransaction {
            val id = reboqueDao.insert(reboque)
            val estoque = EstoqueEntity(reboqueId = id, quantidade = 0)
            estoqueDao.insert(estoque)
            id
        }
    }

    suspend fun cadastrarCliente(cliente: ClienteEntity): Long {
        return clienteDao.insert(cliente)
    }

    suspend fun registrarVenda(reboqueId: Long, clienteId: Long, quantidade: Int): Result<Long> {
        return try {
            database.withTransaction {
                val estoqueCurrent = estoqueDao.getByReboqueId(reboqueId)
                val saldoAtual = estoqueCurrent?.quantidade ?: 0

                if (quantidade > saldoAtual) {
                    return@withTransaction Result.failure(
                        IllegalArgumentException("Máximo disponível: $saldoAtual unidades")
                    )
                }

                val reboque = reboqueDao.getById(reboqueId)
                    ?: return@withTransaction Result.failure(Exception("Reboque não encontrado"))

                val saldoDepois = saldoAtual - quantidade
                val valorUnitario = reboque.valorVendaCents
                val valorTotal = valorUnitario * quantidade

                val venda = VendaEntity(
                    reboqueId = reboqueId,
                    clienteId = clienteId,
                    quantidade = quantidade,
                    valorUnitarioCents = valorUnitario,
                    valorTotalCents = valorTotal,
                    dataHoraTimestamp = System.currentTimeMillis(),
                    saldoAntes = saldoAtual,
                    saldoDepois = saldoDepois
                )

                val vendaId = vendaDao.insert(venda)
                estoqueDao.updateQuantidade(reboqueId, saldoDepois)

                Result.success(vendaId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registrarCompra(reboqueId: Long, quantidade: Int, valorUnitarioCents: Long): Result<Long> {
        return try {
            database.withTransaction {
                val estoqueCurrent = estoqueDao.getByReboqueId(reboqueId)
                val saldoAtual = estoqueCurrent?.quantidade ?: 0
                val saldoDepois = saldoAtual + quantidade
                val valorTotal = valorUnitarioCents * quantidade

                val compra = CompraEntity(
                    reboqueId = reboqueId,
                    quantidade = quantidade,
                    valorUnitarioCents = valorUnitarioCents,
                    valorTotalCents = valorTotal,
                    dataHoraTimestamp = System.currentTimeMillis(),
                    saldoAntes = saldoAtual,
                    saldoDepois = saldoDepois
                )

                val compraId = compraDao.insert(compra)
                estoqueDao.updateQuantidade(reboqueId, saldoDepois)

                Result.success(compraId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registrarAjuste(reboqueId: Long, tipo: TipoAjuste, quantidade: Int, motivo: String): Result<Long> {
        return try {
            database.withTransaction {
                val estoqueCurrent = estoqueDao.getByReboqueId(reboqueId)
                val saldoAtual = estoqueCurrent?.quantidade ?: 0

                if (tipo == TipoAjuste.SAIDA && quantidade > saldoAtual) {
                    return@withTransaction Result.failure(
                        IllegalArgumentException("Saldo insuficiente para saída. Saldo atual: $saldoAtual")
                    )
                }

                val saldoDepois = if (tipo == TipoAjuste.ENTRADA) saldoAtual + quantidade else saldoAtual - quantidade

                val ajuste = AjusteEstoqueEntity(
                    reboqueId = reboqueId,
                    tipo = tipo,
                    quantidade = quantidade,
                    motivo = motivo,
                    dataHoraTimestamp = System.currentTimeMillis(),
                    saldoAntes = saldoAtual,
                    saldoDepois = saldoDepois
                )

                val ajusteId = ajusteDao.insert(ajuste)
                estoqueDao.updateQuantidade(reboqueId, saldoDepois)

                Result.success(ajusteId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
