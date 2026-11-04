package br.edu.ifpe.jsreboques.data.remote.api

import br.edu.ifpe.jsreboques.data.remote.dto.FornecedorDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FornecedorApi {

    @GET("fornecedores")
    suspend fun getFornecedores(): List<FornecedorDto>

    @GET("fornecedores/{id}")
    suspend fun getFornecedorById(@Path("id") id: Long): FornecedorDto

    @POST("fornecedores")
    suspend fun criarFornecedor(@Body fornecedor: FornecedorDto): FornecedorDto

    @PUT("fornecedores/{id}")
    suspend fun atualizarFornecedor(
        @Path("id") id: Long,
        @Body fornecedor: FornecedorDto
    ): FornecedorDto

    @DELETE("fornecedores/{id}")
    suspend fun deletarFornecedor(@Path("id") id: Long): Response<Unit>
}
