package br.edu.ifpe.jsreboques.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reboques")
data class ReboqueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val marca: String,
    val modelo: String,
    val categoria: String,
    val descricao: String = "",
    val valorCompraCents: Long,
    val valorVendaCents: Long
)
