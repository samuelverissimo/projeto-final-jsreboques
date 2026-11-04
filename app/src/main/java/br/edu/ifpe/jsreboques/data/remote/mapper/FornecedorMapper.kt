package br.edu.ifpe.jsreboques.data.remote.mapper

import br.edu.ifpe.jsreboques.data.local.entity.FornecedorEntity
import br.edu.ifpe.jsreboques.data.remote.dto.FornecedorDto

fun FornecedorDto.toEntity(): FornecedorEntity {
    return FornecedorEntity(
        id = this.id ?: 0L,
        nome = this.nome,
        contato = this.contato,
        documento = this.documento
    )
}

fun FornecedorEntity.toDto(): FornecedorDto {
    return FornecedorDto(
        id = if (this.id == 0L) null else this.id,
        nome = this.nome,
        contato = this.contato,
        documento = this.documento
    )
}
