package br.edu.ifpe.jsreboques.model

data class Cliente(
    val id: Long = 0,
    val nome: String,
    val email: String,
    val whatsapp: String
)
