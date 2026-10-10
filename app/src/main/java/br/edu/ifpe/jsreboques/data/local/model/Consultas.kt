package br.edu.ifpe.jsreboques.data.local.model

import androidx.room.Embedded
import br.edu.ifpe.jsreboques.data.local.entity.ReboqueEntity

/** Reboque + saldo de estoque (resultado de um JOIN). */
data class ReboqueComEstoque(
    @Embedded val reboque: ReboqueEntity,
    val quantidade: Int
)

data class ResumoVendas(
    val totalCents: Long,
    val vendas: Int,
    val unidades: Int
)

data class VendaHistorico(
    val id: Long,
    val quantidade: Int,
    val valorTotalCents: Long,
    val estoqueAntes: Int,
    val estoqueDepois: Int,
    val dataHoraTimestamp: Long,
    val reboqueNome: String,
    val clienteNome: String
)

data class CompraHistorico(
    val id: Long,
    val quantidade: Int,
    val valorUnitarioCents: Long,
    val valorTotalCents: Long,
    val estoqueAntes: Int,
    val estoqueDepois: Int,
    val dataHoraTimestamp: Long,
    val reboqueNome: String
)

data class AjusteHistorico(
    val id: Long,
    val quantidade: Int,
    val motivo: String,
    val estoqueAntes: Int,
    val estoqueDepois: Int,
    val dataHoraTimestamp: Long,
    val reboqueNome: String
)
