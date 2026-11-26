package br.edu.ifpe.jsreboques.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.edu.ifpe.jsreboques.data.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE username = :username LIMIT 1")
    suspend fun buscarPorUsername(username: String): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuario: UsuarioEntity): Long

    @Insert
    suspend fun inserir(usuario: UsuarioEntity): Long
}
