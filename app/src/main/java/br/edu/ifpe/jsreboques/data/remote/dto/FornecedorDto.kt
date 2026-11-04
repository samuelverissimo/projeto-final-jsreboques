package br.edu.ifpe.jsreboques.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FornecedorDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nome") val nome: String,
    @SerializedName("contato") val contato: String,
    @SerializedName("documento") val documento: String? = null
)
