package br.edu.ifpe.jsreboques.model

data class EstoqueReboque(
    val id: Long = 0,
    val nome: String,
    val marca: String,
    val modelo: String,
    val categoria: String,
    val valorCompra: Double,
    val valorVenda: Double,
    val quantidade: Int,
    val descricao: String
)
