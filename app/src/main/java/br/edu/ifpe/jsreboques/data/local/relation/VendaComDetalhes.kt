package br.edu.ifpe.jsreboques.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import br.edu.ifpe.jsreboques.data.local.entity.ClienteEntity
import br.edu.ifpe.jsreboques.data.local.entity.ReboqueEntity
import br.edu.ifpe.jsreboques.data.local.entity.VendaEntity

data class VendaComDetalhes(
    @Embedded val venda: VendaEntity,
    @Relation(
        parentColumn = "reboqueId",
        entityColumn = "id"
    )
    val reboque: ReboqueEntity,
    @Relation(
        parentColumn = "clienteId",
        entityColumn = "id"
    )
    val cliente: ClienteEntity
)
