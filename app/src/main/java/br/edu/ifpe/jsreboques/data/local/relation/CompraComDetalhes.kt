package br.edu.ifpe.jsreboques.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import br.edu.ifpe.jsreboques.data.local.entity.CompraEntity
import br.edu.ifpe.jsreboques.data.local.entity.ReboqueEntity

data class CompraComDetalhes(
    @Embedded val compra: CompraEntity,
    @Relation(
        parentColumn = "reboqueId",
        entityColumn = "id"
    )
    val reboque: ReboqueEntity
)
