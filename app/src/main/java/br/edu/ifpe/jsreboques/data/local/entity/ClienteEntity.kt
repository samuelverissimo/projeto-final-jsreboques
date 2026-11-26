package br.edu.ifpe.jsreboques.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TipoCliente {
    FISICA, JURIDICA
}

@Entity(tableName = "clientes")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val email: String,
    val whatsapp: String,
    val documento: String = "",
    val tipo: TipoCliente = TipoCliente.FISICA
)
