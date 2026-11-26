package br.edu.ifpe.jsreboques.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.edu.ifpe.jsreboques.data.local.entity.AjusteEstoqueEntity
import br.edu.ifpe.jsreboques.data.local.relation.AjusteComDetalhes
import kotlinx.coroutines.flow.Flow

@Dao
interface AjusteEstoqueDao {
    @Transaction
    @Query("SELECT * FROM ajustes_estoque ORDER BY dataHoraTimestamp DESC")
    fun getAllAjustesComDetalhes(): Flow<List<AjusteComDetalhes>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ajuste: AjusteEstoqueEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ajustes: List<AjusteEstoqueEntity>): List<Long>
}
