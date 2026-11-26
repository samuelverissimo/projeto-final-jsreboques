package br.edu.ifpe.jsreboques.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import br.edu.ifpe.jsreboques.data.local.entity.EstoqueEntity
import br.edu.ifpe.jsreboques.data.local.entity.ReboqueEntity

data class ReboqueComEstoque(
    @Embedded val reboque: ReboqueEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "reboqueId"
    )
    val estoque: EstoqueEntity?
) {
    val quantidadeEstoque: Int
        get() = estoque?.quantidade ?: 0
}
