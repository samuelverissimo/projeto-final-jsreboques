package br.edu.ifpe.jsreboques.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import br.edu.ifpe.jsreboques.data.local.entity.AjusteEstoqueEntity
import br.edu.ifpe.jsreboques.data.local.entity.ReboqueEntity

data class AjusteComDetalhes(
    @Embedded val ajuste: AjusteEstoqueEntity,
    @Relation(
        parentColumn = "reboqueId",
        entityColumn = "id"
    )
    val reboque: ReboqueEntity
)
