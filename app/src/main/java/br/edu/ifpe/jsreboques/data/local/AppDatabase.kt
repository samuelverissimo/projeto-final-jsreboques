package br.edu.ifpe.jsreboques.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import br.edu.ifpe.jsreboques.data.local.dao.ClienteDao
import br.edu.ifpe.jsreboques.data.local.dao.FornecedorDao
import br.edu.ifpe.jsreboques.data.local.dao.OperacaoDao
import br.edu.ifpe.jsreboques.data.local.dao.ReboqueDao
import br.edu.ifpe.jsreboques.data.local.dao.TarefaDao
import br.edu.ifpe.jsreboques.data.local.dao.UsuarioDao
import br.edu.ifpe.jsreboques.data.local.entity.AjusteEstoqueEntity
import br.edu.ifpe.jsreboques.data.local.entity.ClienteEntity
import br.edu.ifpe.jsreboques.data.local.entity.CompraEntity
import br.edu.ifpe.jsreboques.data.local.entity.EstoqueEntity
import br.edu.ifpe.jsreboques.data.local.entity.FornecedorEntity
import br.edu.ifpe.jsreboques.data.local.entity.ReboqueEntity
import br.edu.ifpe.jsreboques.data.local.entity.TarefaEntity
import br.edu.ifpe.jsreboques.data.local.entity.UsuarioEntity
import br.edu.ifpe.jsreboques.data.local.entity.VendaEntity

/** Coloque false para o app abrir sem os dados de exemplo das telas. */
private const val POPULAR_DADOS_DEMO = true

@Database(
    entities = [
        ReboqueEntity::class,
        EstoqueEntity::class,
        ClienteEntity::class,
        VendaEntity::class,
        CompraEntity::class,
        AjusteEstoqueEntity::class,
        UsuarioEntity::class,
        FornecedorEntity::class,
        TarefaEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reboqueDao(): ReboqueDao
    abstract fun clienteDao(): ClienteDao
    abstract fun operacaoDao(): OperacaoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun fornecedorDao(): FornecedorDao
    abstract fun tarefaDao(): TarefaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "js_reboques.db"
                )
                    .addCallback(SementeDemo)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Método antigo mantido por compatibilidade com AppContainer, 
         * agora chama internamente o Singleton.
         */
        fun criar(context: Context): AppDatabase = getDatabase(context)

        // Roda uma única vez, quando o banco é criado pela primeira vez.
        // DICA: se mudar as entidades durante o desenvolvimento, desinstale o app
        // do emulador para recriar o banco (ou crie uma Migration).
        private val SementeDemo = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                if (!POPULAR_DADOS_DEMO) return
                db.execSQL(
                    "INSERT INTO reboques (id, nome, marca, modelo, categoria, descricao, valorCompraCents, valorVendaCents) VALUES " +
                        "(1,'Utilitário 500 kg','JS Reboques','UT-500','Utilitário','',340000,489000)," +
                        "(2,'Moto Duplo','JS Reboques','MT-02','Moto','',440000,629000)," +
                        "(3,'Carga 750 kg','JS Reboques','CG-750','Carga','',520000,749000)," +
                        "(4,'Plataforma 1.000 kg','JS Reboques','PL-1000','Plataforma','',700000,989000)"
                )
                db.execSQL("INSERT INTO estoques (reboqueId, quantidade) VALUES (1,8),(2,2),(3,0),(4,5)")
                db.execSQL(
                    "INSERT INTO clientes (id, nome, email, whatsapp, documento) VALUES " +
                        "(1,'Carlos Henrique Silva','carlos.silva@example.com','11900000101','11122233301')," +
                        "(2,'Agro Vale Transportes','compras.agrovale@example.com','19900000202','12345678000102')," +
                        "(3,'Mariana Oliveira Costa','mariana.costa@example.com','15900000303','11122233303')"
                )
            }
        }
    }
}
