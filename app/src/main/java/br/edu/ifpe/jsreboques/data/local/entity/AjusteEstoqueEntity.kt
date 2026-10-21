package br.edu.ifpe.jsreboques.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Entrada/saída manual. quantidade > 0 = entrada, quantidade < 0 = saída. */
@Entity(
    tableName = "ajustes_estoque",
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
data class AjusteEstoqueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reboqueId: Long,
    val quantidade: Int,
    val motivo: String,
    val estoqueAntes: Int,
    val estoqueDepois: Int,
    val dataHoraTimestamp: Long = System.currentTimeMillis(),
)
