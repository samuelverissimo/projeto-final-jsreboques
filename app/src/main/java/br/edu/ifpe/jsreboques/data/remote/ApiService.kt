package br.edu.ifpe.jsreboques.data.remote

import br.edu.ifpe.jsreboques.data.local.entity.ClienteEntity
import br.edu.ifpe.jsreboques.data.local.entity.ReboqueEntity
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("reboques")
    suspend fun getReboques(): Response<List<ReboqueEntity>>

    @GET("clientes")
    suspend fun getClientes(): Response<List<ClienteEntity>>
}
