package br.edu.ifpe.jsreboques.data.local.dao

import androidx.room.*
import br.edu.ifpe.jsreboques.data.local.entity.TarefaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TarefaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(tarefa: TarefaEntity)

    @Update
    suspend fun atualizar(tarefa: TarefaEntity)

    @Delete
    suspend fun deletar(tarefa: TarefaEntity)

    @Query("SELECT * FROM tarefas WHERE id = :id")
    suspend fun buscarPorId(id: Int): TarefaEntity?

    @Query("SELECT * FROM tarefas ORDER BY id DESC")
    fun listarTodas(): Flow<List<TarefaEntity>>

    @Query("DELETE FROM tarefas")
    suspend fun deletarTodas()
}
