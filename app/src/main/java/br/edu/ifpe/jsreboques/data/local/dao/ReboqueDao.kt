package br.edu.ifpe.jsreboques.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import br.edu.ifpe.jsreboques.data.local.entity.EstoqueEntity
import br.edu.ifpe.jsreboques.data.local.entity.ReboqueEntity
import br.edu.ifpe.jsreboques.data.local.relation.ReboqueComEstoque
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ReboqueDao {

    @Query(
        """
        SELECT r.*, COALESCE(e.quantidade, 0) AS quantidade
        FROM reboques r
        LEFT JOIN estoques e ON e.reboqueId = r.id
        ORDER BY r.nome COLLATE NOCASE ASC
        """
    )
    abstract fun observarTodos(): Flow<List<br.edu.ifpe.jsreboques.data.local.model.ReboqueComEstoque>>

    @Transaction
    @Query("SELECT * FROM reboques ORDER BY nome ASC")
    abstract fun getReboquesComEstoque(): Flow<List<ReboqueComEstoque>>

    @Transaction
    @Query("SELECT * FROM reboques WHERE id = :id")
    abstract suspend fun getReboqueComEstoqueById(id: Long): ReboqueComEstoque?

    @Query("SELECT * FROM reboques WHERE id = :id")
    abstract suspend fun getById(id: Long): ReboqueEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(reboque: ReboqueEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(reboques: List<ReboqueEntity>): List<Long>

    @Insert
    abstract suspend fun inserir(reboque: ReboqueEntity): Long

    @Update
    abstract suspend fun update(reboque: ReboqueEntity)

    @Update
    abstract suspend fun atualizar(reboque: ReboqueEntity)

    @Query("DELETE FROM reboques WHERE id = :id")
    abstract suspend fun excluir(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertEstoque(estoque: EstoqueEntity)

    @Transaction
    open suspend fun cadastrar(reboque: ReboqueEntity): Long {
        val id = inserir(reboque)
        insertEstoque(EstoqueEntity(reboqueId = id, quantidade = 0))
        return id
    }
}
