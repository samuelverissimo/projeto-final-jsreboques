package br.edu.ifpe.jsreboques.data.local.entity

data class ResumoVendas(
    val totalCents: Long = 0,
    val vendas: Int = 0,
    val unidades: Int = 0
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

