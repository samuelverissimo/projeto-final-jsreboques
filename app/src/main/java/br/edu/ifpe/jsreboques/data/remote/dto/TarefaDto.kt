package br.edu.ifpe.jsreboques.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TarefaDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descricao") val descricao: String,
    @SerializedName("concluida") val concluida: Boolean = false
)
