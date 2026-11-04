package br.edu.ifpe.jsreboques.data.remote.api

import br.edu.ifpe.jsreboques.data.remote.dto.ClienteDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ClienteApi {

    @GET("clientes")
    suspend fun getClientes(): List<ClienteDto>

    @GET("clientes/{id}")
    suspend fun getClienteById(@Path("id") id: Long): ClienteDto

    @POST("clientes")
    suspend fun criarCliente(@Body cliente: ClienteDto): ClienteDto

    @PUT("clientes/{id}")
    suspend fun atualizarCliente(
        @Path("id") id: Long,
        @Body cliente: ClienteDto
    ): ClienteDto

    @DELETE("clientes/{id}")
    suspend fun deletarCliente(@Path("id") id: Long): Response<Unit>
}
