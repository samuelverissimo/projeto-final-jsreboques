package br.edu.ifpe.jsreboques.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "clientes")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val email: String,
    val whatsapp: String,
    val documento: String = "",
)

@Entity(
    tableName = "vendas",
    foreignKeys = [
        ForeignKey(
            entity = ReboqueEntity::class,
            parentColumns = ["id"],
            childColumns = ["reboqueId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("reboqueId"), Index("clienteId")],
)
data class VendaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reboqueId: Long,
    val clienteId: Long,
    val quantidade: Int,
    val valorUnitarioCents: Long,
    val valorTotalCents: Long,
    val estoqueAntes: Int = 0,
    val estoqueDepois: Int = 0,
    val dataHoraTimestamp: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "compras",
    foreignKeys = [
        ForeignKey(
            entity = ReboqueEntity::class,
            parentColumns = ["id"],
            childColumns = ["reboqueId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("reboqueId")],
)
data class CompraEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reboqueId: Long,
    val quantidade: Int,
    val valorUnitarioCents: Long,
    val valorTotalCents: Long,
    val estoqueAntes: Int = 0,
    val estoqueDepois: Int = 0,
    val dataHoraTimestamp: Long = System.currentTimeMillis(),
)
