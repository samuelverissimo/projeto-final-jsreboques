package br.edu.ifpe.jsreboques.data.remote.mapper

import br.edu.ifpe.jsreboques.data.local.entity.ClienteEntity
import br.edu.ifpe.jsreboques.data.remote.dto.ClienteDto

fun ClienteDto.toEntity(): ClienteEntity {
    return ClienteEntity(
        id = this.id ?: 0L,
        nome = this.nome,
        email = this.email,
        telefone = this.telefone,
        documento = this.documento
    )
}

fun ClienteEntity.toDto(): ClienteDto {
    return ClienteDto(
        id = if (this.id == 0L) null else this.id,
        nome = this.nome,
        email = this.email,
        telefone = this.telefone,
        documento = this.documento
    )
}
