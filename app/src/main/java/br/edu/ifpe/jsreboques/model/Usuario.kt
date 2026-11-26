package br.edu.ifpe.jsreboques.model

data class Usuario(
    val id: Long = 0,
    val nome: String,
    val usuario: String,
    val senha: String,
    val tipo: String
)
