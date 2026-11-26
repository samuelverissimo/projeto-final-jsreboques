package br.edu.ifpe.jsreboques.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import br.edu.ifpe.jsreboques.data.local.entity.ClienteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Query("SELECT * FROM clientes ORDER BY nome ASC")
    fun getAllClientes(): Flow<List<ClienteEntity>>

    @Query("SELECT * FROM clientes ORDER BY nome COLLATE NOCASE ASC")
    fun observarTodos(): Flow<List<ClienteEntity>>

    @Query("SELECT * FROM clientes WHERE id = :id")
    suspend fun getById(id: Long): ClienteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cliente: ClienteEntity): Long

    @Insert
    suspend fun inserir(cliente: ClienteEntity): Long

    @Update
    suspend fun atualizar(cliente: ClienteEntity)

    @Upsert
    suspend fun sincronizar(clientes: List<ClienteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clientes: List<ClienteEntity>): List<Long>
}
