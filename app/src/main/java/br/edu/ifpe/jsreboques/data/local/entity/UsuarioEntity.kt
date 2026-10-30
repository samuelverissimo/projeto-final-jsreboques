package br.edu.ifpe.jsreboques.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Tabela pronta para o Login (F7, opcional) — ainda sem tela. */
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val username: String,
    val passwordHash: String,
    val perfil: String, // "ADMIN" ou "OPERADOR"
)
