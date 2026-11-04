package br.edu.ifpe.jsreboques.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Compra(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val estoqueReboqueId: Long,
    val data: String,
    val quantidade: Int,
    val valorUnitario: Double,
    val valorTotal: Double
)
