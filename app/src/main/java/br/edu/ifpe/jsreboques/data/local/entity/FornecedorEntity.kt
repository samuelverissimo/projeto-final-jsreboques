package br.edu.ifpe.jsreboques.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fornecedores")
data class FornecedorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val contato: String,
    val documento: String? = null
)
