package br.edu.ifpe.jsreboques.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
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

@Entity(
    tableName = "estoques",
    foreignKeys = [
        ForeignKey(
            entity = ReboqueEntity::class,
            parentColumns = ["id"],
            childColumns = ["reboqueId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EstoqueEntity(
    @PrimaryKey val reboqueId: Long,
    val quantidade: Int
)

@Entity(tableName = "clientes")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val email: String,
    val whatsapp: String,
    val documento: String = ""
)

@Entity(
    tableName = "vendas",
    foreignKeys = [
        ForeignKey(
            entity = ReboqueEntity::class,
            parentColumns = ["id"],
            childColumns = ["reboqueId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("reboqueId"), Index("clienteId")]
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
    val dataHoraTimestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "compras",
    foreignKeys = [
        ForeignKey(
            entity = ReboqueEntity::class,
            parentColumns = ["id"],
            childColumns = ["reboqueId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("reboqueId")]
)
data class CompraEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reboqueId: Long,
    val quantidade: Int,
    val valorUnitarioCents: Long,
    val valorTotalCents: Long,
    val estoqueAntes: Int = 0,
    val estoqueDepois: Int = 0,
    val dataHoraTimestamp: Long = System.currentTimeMillis()
)

/** Entrada/saída manual. quantidade > 0 = entrada, quantidade < 0 = saída. */
@Entity(
    tableName = "ajustes_estoque",
    foreignKeys = [
        ForeignKey(
            entity = ReboqueEntity::class,
            parentColumns = ["id"],
            childColumns = ["reboqueId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("reboqueId")]
)
data class AjusteEstoqueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reboqueId: Long,
    val quantidade: Int,
    val motivo: String,
    val estoqueAntes: Int,
    val estoqueDepois: Int,
    val dataHoraTimestamp: Long = System.currentTimeMillis()
)

/** Tabela pronta para o Login (F7, opcional) — ainda sem tela. */
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val username: String,
    val passwordHash: String,
    val perfil: String // "ADMIN" ou "OPERADOR"
)
