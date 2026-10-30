package br.edu.ifpe.jsreboques.data.remote

import br.edu.ifpe.jsreboques.data.remote.api.ClienteApi
import br.edu.ifpe.jsreboques.data.remote.api.FornecedorApi
import br.edu.ifpe.jsreboques.data.remote.api.TarefaApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://api.jsreboques.com.br/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val clienteApi: ClienteApi by lazy {
        retrofit.create(ClienteApi::class.java)
    }

    val tarefaApi: TarefaApi by lazy {
        retrofit.create(TarefaApi::class.java)
    }

    val fornecedorApi: FornecedorApi by lazy {
        retrofit.create(FornecedorApi::class.java)
    }
}
