package br.edu.ifpe.jsreboques.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.edu.ifpe.jsreboques.data.local.entity.CompraEntity
import br.edu.ifpe.jsreboques.data.local.relation.CompraComDetalhes
import kotlinx.coroutines.flow.Flow

@Dao
interface CompraDao {
    @Transaction
    @Query("SELECT * FROM compras ORDER BY dataHoraTimestamp DESC")
    fun getAllComprasComDetalhes(): Flow<List<CompraComDetalhes>>

    @Transaction
    @Query("SELECT * FROM compras WHERE id = :id")
    suspend fun getCompraComDetalhesById(id: Long): CompraComDetalhes?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(compra: CompraEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(compras: List<CompraEntity>): List<Long>
}
