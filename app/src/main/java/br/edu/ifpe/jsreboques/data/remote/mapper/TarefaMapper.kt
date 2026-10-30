package br.edu.ifpe.jsreboques.data.remote.mapper

import br.edu.ifpe.jsreboques.data.local.entity.TarefaEntity
import br.edu.ifpe.jsreboques.data.remote.dto.TarefaDto

fun TarefaDto.toEntity(): TarefaEntity {
    return TarefaEntity(
        id = this.id ?: 0,
        titulo = this.titulo,
        descricao = this.descricao,
        concluida = this.concluida
    )
}

fun TarefaEntity.toDto(): TarefaDto {
    return TarefaDto(
        id = if (this.id == 0) null else this.id,
        titulo = this.titulo,
        descricao = this.descricao,
        concluida = this.concluida
    )
}
