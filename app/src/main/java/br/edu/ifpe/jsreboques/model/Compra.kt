package br.edu.ifpe.jsreboques.model

data class Compra(
    val id: Long = 0,
    val estoqueReboqueId: Long,
    val data: String,
    val quantidade: Int,
    val valorUnitario: Double,
    val valorTotal: Double
)
