package br.edu.ifpe.jsreboques.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.edu.ifpe.jsreboques.data.local.entity.EstoqueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EstoqueDao {
    @Query("SELECT * FROM estoques WHERE reboqueId = :reboqueId")
    suspend fun getByReboqueId(reboqueId: Long): EstoqueEntity?

    @Query("SELECT * FROM estoques")
    fun getAllEstoque(): Flow<List<EstoqueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(estoque: EstoqueEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(estoques: List<EstoqueEntity>)

    @Update
    suspend fun update(estoque: EstoqueEntity)

    @Query("UPDATE estoques SET quantidade = :novaQuantidade WHERE reboqueId = :reboqueId")
    suspend fun updateQuantidade(reboqueId: Long, novaQuantidade: Int): Int
}
