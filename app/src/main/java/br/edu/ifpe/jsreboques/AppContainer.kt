package br.edu.ifpe.jsreboques

import android.content.Context
import br.edu.ifpe.jsreboques.data.local.AppDatabase
import br.edu.ifpe.jsreboques.data.remote.RetrofitClient
import br.edu.ifpe.jsreboques.data.repository.ClienteRepository
import br.edu.ifpe.jsreboques.data.repository.FornecedorRepository
import br.edu.ifpe.jsreboques.data.repository.OperacaoRepository
import br.edu.ifpe.jsreboques.data.repository.ReboqueRepository
import br.edu.ifpe.jsreboques.data.repository.TarefaRepository

/**
 * Injeção de dependência "manual" (simples para iniciantes).
 * Todo ViewModel recebe daqui os repositórios de que precisa.
 */
class AppContainer(context: Context) {

    private val db = AppDatabase.getDatabase(context)

    val reboqueRepository = ReboqueRepository(db.reboqueDao())
    val clienteRepository = ClienteRepository(db.clienteDao(), RetrofitClient.clienteApi)
    val operacaoRepository = OperacaoRepository(db.operacaoDao())
    val tarefaRepository = TarefaRepository(db.tarefaDao())
    val fornecedorRepository = FornecedorRepository(db.fornecedorDao())
}
