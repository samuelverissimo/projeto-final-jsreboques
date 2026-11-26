package br.edu.ifpe.jsreboques.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.edu.ifpe.jsreboques.data.local.entity.VendaEntity
import br.edu.ifpe.jsreboques.data.local.relation.VendaComDetalhes
import kotlinx.coroutines.flow.Flow

@Dao
interface VendaDao {
    @Transaction
    @Query("SELECT * FROM vendas ORDER BY dataHoraTimestamp DESC")
    fun getAllVendasComDetalhes(): Flow<List<VendaComDetalhes>>

    @Transaction
    @Query("SELECT * FROM vendas WHERE id = :id")
    suspend fun getVendaComDetalhesById(id: Long): VendaComDetalhes?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(venda: VendaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vendas: List<VendaEntity>): List<Long>
}
