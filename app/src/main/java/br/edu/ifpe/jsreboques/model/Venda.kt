package br.edu.ifpe.jsreboques.model

data class Venda(
    val id: Long = 0,
    val clienteId: Long,
    val estoqueReboqueId: Long,
    val data: String,
    val quantidade: Int,
    val valorUnitario: Double,
    val valorTotal: Double
)
