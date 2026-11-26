package br.edu.ifpe.jsreboques.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TipoAjuste {
    ENTRADA, SAIDA
}

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
    val tipo: TipoAjuste,
    val quantidade: Int,
    val motivo: String,
    val dataHoraTimestamp: Long = System.currentTimeMillis(),
    val saldoAntes: Int,
    val saldoDepois: Int
) {
    val codigoFormatado: String
        get() = "A-%03d".format(id)
}
