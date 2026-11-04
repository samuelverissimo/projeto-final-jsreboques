package br.edu.ifpe.jsreboques.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

data class ReboqueDto(
    val id: Long,
    val nome: String,
    val marca: String,
    val modelo: String,
    val categoria: String,
    val valorCompraCents: Long,
    val valorVendaCents: Long
)

data class ClienteDto(
    val id: Long,
    val nome: String,
    val email: String,
    val whatsapp: String,
    val documento: String
)

interface ApiService {
    @GET("reboques")
    suspend fun listarReboques(): List<ReboqueDto>

    @GET("clientes")
    suspend fun listarClientes(): List<ClienteDto>
}

object RetrofitFactory {
    private const val BASE_URL = "https://api.example.com/" // TODO: trocar pela URL real

    fun criar(): ApiService {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
