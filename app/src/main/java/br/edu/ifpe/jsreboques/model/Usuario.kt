package br.edu.ifpe.jsreboques.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val usuario: String,
    val senha: String,
    val tipo: String
)
