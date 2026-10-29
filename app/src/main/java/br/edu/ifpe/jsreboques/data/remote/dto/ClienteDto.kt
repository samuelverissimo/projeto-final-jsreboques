package br.edu.ifpe.jsreboques.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ClienteDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nome") val nome: String,
    @SerializedName("email") val email: String,
    @SerializedName("telefone") val telefone: String,
    @SerializedName("documento") val documento: String
)
