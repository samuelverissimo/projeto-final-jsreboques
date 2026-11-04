package br.edu.ifpe.jsreboques.data.remote.api

import br.edu.ifpe.jsreboques.data.remote.dto.TarefaDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TarefaApi {

    @GET("tarefas")
    suspend fun getTarefas(): List<TarefaDto>

    @GET("tarefas/{id}")
    suspend fun getTarefaById(@Path("id") id: Int): TarefaDto

    @POST("tarefas")
    suspend fun criarTarefa(@Body tarefa: TarefaDto): TarefaDto

    @PUT("tarefas/{id}")
    suspend fun atualizarTarefa(
        @Path("id") id: Int,
        @Body tarefa: TarefaDto
    ): TarefaDto

    @DELETE("tarefas/{id}")
    suspend fun deletarTarefa(@Path("id") id: Int): Response<Unit>
}
