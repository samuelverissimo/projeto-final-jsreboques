package br.edu.ifpe.jsreboques.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cliente(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val email: String,
    val whatsapp: String
)
