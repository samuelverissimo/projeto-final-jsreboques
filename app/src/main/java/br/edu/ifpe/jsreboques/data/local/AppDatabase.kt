package br.edu.ifpe.jsreboques.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import br.edu.ifpe.jsreboques.data.local.dao.AjusteEstoqueDao
import br.edu.ifpe.jsreboques.data.local.dao.ClienteDao
import br.edu.ifpe.jsreboques.data.local.dao.CompraDao
import br.edu.ifpe.jsreboques.data.local.dao.EstoqueDao
import br.edu.ifpe.jsreboques.data.local.dao.FornecedorDao
import br.edu.ifpe.jsreboques.data.local.dao.OperacaoDao
import br.edu.ifpe.jsreboques.data.local.dao.ReboqueDao
import br.edu.ifpe.jsreboques.data.local.dao.TarefaDao
import br.edu.ifpe.jsreboques.data.local.dao.UsuarioDao
import br.edu.ifpe.jsreboques.data.local.dao.VendaDao
import br.edu.ifpe.jsreboques.data.local.entity.AjusteEstoqueEntity
import br.edu.ifpe.jsreboques.data.local.entity.ClienteEntity
import br.edu.ifpe.jsreboques.data.local.entity.CompraEntity
import br.edu.ifpe.jsreboques.data.local.entity.EstoqueEntity
import br.edu.ifpe.jsreboques.data.local.entity.FornecedorEntity
import br.edu.ifpe.jsreboques.data.local.entity.ReboqueEntity
import br.edu.ifpe.jsreboques.data.local.entity.TarefaEntity
import br.edu.ifpe.jsreboques.data.local.entity.TipoAjuste
import br.edu.ifpe.jsreboques.data.local.entity.TipoCliente
import br.edu.ifpe.jsreboques.data.local.entity.UsuarioEntity
import br.edu.ifpe.jsreboques.data.local.entity.VendaEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [
        ReboqueEntity::class,
        EstoqueEntity::class,
        ClienteEntity::class,
        VendaEntity::class,
        CompraEntity::class,
        AjusteEstoqueEntity::class,
        UsuarioEntity::class,
        TarefaEntity::class,
        FornecedorEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reboqueDao(): ReboqueDao
    abstract fun estoqueDao(): EstoqueDao
    abstract fun clienteDao(): ClienteDao
    abstract fun vendaDao(): VendaDao
    abstract fun compraDao(): CompraDao
    abstract fun ajusteEstoqueDao(): AjusteEstoqueDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun operacaoDao(): OperacaoDao
    abstract fun tarefaDao(): TarefaDao
    abstract fun fornecedorDao(): FornecedorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase = getInstance(context)

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "js_reboques.db"
                )
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    seedDatabase(getInstance(context))
                }
            }
        }

        private suspend fun seedDatabase(database: AppDatabase) {
            val reboqueDao = database.reboqueDao()
            val estoqueDao = database.estoqueDao()
            val clienteDao = database.clienteDao()
            val vendaDao = database.vendaDao()
            val compraDao = database.compraDao()
            val ajusteDao = database.ajusteEstoqueDao()
            val usuarioDao = database.usuarioDao()

            // 1. Reboques
            val r1 = ReboqueEntity(id = 1, nome = "Carga 750 kg", marca = "JS Reboques", modelo = "CG-750", categoria = "Carga", descricao = "Reboque leve para cargas até 750kg", valorCompraCents = 520000L, valorVendaCents = 749000L)
            val r2 = ReboqueEntity(id = 2, nome = "Moto Duplo", marca = "JS Reboques", modelo = "MT-02", categoria = "Moto", descricao = "Transporte de até duas motocicletas", valorCompraCents = 440000L, valorVendaCents = 629000L)
            val r3 = ReboqueEntity(id = 3, nome = "Plataforma 1.000 kg", marca = "JS Reboques", modelo = "PL-1000", categoria = "Plataforma", descricao = "Plataforma reforçada para veículos e máquinas", valorCompraCents = 700000L, valorVendaCents = 989000L)
            val r4 = ReboqueEntity(id = 4, nome = "Utilitário 500 kg", marca = "JS Reboques", modelo = "UT-500", categoria = "Utilitário", descricao = "Modelo compacto multiuso", valorCompraCents = 340000L, valorVendaCents = 489000L)

            reboqueDao.insertAll(listOf(r1, r2, r3, r4))

            // Current Stock balances
            estoqueDao.insertAll(
                listOf(
                    EstoqueEntity(reboqueId = 1, quantidade = 0),
                    EstoqueEntity(reboqueId = 2, quantidade = 2),
                    EstoqueEntity(reboqueId = 3, quantidade = 5),
                    EstoqueEntity(reboqueId = 4, quantidade = 8)
                )
            )

            // 2. Clientes
            val c1 = ClienteEntity(id = 1, nome = "Carlos Henrique Silva", email = "carlos.silva@example.com", whatsapp = "(11) 90000-0101", documento = "***.***.***-01", tipo = TipoCliente.FISICA)
            val c2 = ClienteEntity(id = 2, nome = "Agro Vale Transportes", email = "compras.agrovale@example.com", whatsapp = "(19) 90000-0202", documento = "**.***.***/****-02", tipo = TipoCliente.JURIDICA)
            val c3 = ClienteEntity(id = 3, nome = "Mariana Oliveira Costa", email = "mariana.costa@example.com", whatsapp = "(15) 90000-0303", documento = "***.***.***-03", tipo = TipoCliente.FISICA)

            clienteDao.insertAll(listOf(c1, c2, c3))

            // 3. User
            usuarioDao.insert(
                UsuarioEntity(id = 1, nome = "João Pedro", username = "admin", passwordHash = "admin123", perfil = "ADMIN")
            )

            // Timestamps helper (October 2026)
            fun getTs(day: Int, hour: Int, min: Int): Long {
                val cal = Calendar.getInstance()
                cal.set(2026, Calendar.OCTOBER, day, hour, min, 0)
                cal.set(Calendar.MILLISECOND, 0)
                return cal.timeInMillis
            }

            // 4. Seed History (October 2026)
            // V-001 (02 out. 15:40, Plataforma 1.000 kg, Agro Vale, -2 un., R$ 19.780,00, 7->5)
            val v1 = VendaEntity(
                id = 1,
                reboqueId = 3,
                clienteId = 2,
                quantidade = 2,
                valorUnitarioCents = 989000L,
                valorTotalCents = 1978000L,
                dataHoraTimestamp = getTs(2, 15, 40),
                saldoAntes = 7,
                saldoDepois = 5
            )

            // C-001 (03 out. 09:10, Utilitário 500 kg, +5 un., R$ 3.400,00/un., total R$ 17.000,00, 6->11)
            val cp1 = CompraEntity(
                id = 1,
                reboqueId = 4,
                quantidade = 5,
                valorUnitarioCents = 340000L,
                valorTotalCents = 1700000L,
                dataHoraTimestamp = getTs(3, 9, 10),
                saldoAntes = 6,
                saldoDepois = 11
            )

            // V-002 (04 out. 11:05, Moto Duplo, Mariana, -1 un., R$ 6.290,00, 3->2)
            val v2 = VendaEntity(
                id = 2,
                reboqueId = 2,
                clienteId = 3,
                quantidade = 1,
                valorUnitarioCents = 629000L,
                valorTotalCents = 629000L,
                dataHoraTimestamp = getTs(4, 11, 5),
                saldoAntes = 3,
                saldoDepois = 2
            )

            // V-003 (05 out. 14:30, Utilitário 500 kg, Carlos, -1 un., R$ 4.890,00, 11->10)
            val v3 = VendaEntity(
                id = 3,
                reboqueId = 4,
                clienteId = 1,
                quantidade = 1,
                valorUnitarioCents = 489000L,
                valorTotalCents = 489000L,
                dataHoraTimestamp = getTs(5, 14, 30),
                saldoAntes = 11,
                saldoDepois = 10
            )

            // A-001 (06 out. 10:15, Carga 750 kg, saída manual uso interno, -1 un., 1->0)
            val a1 = AjusteEstoqueEntity(
                id = 1,
                reboqueId = 1,
                tipo = TipoAjuste.SAIDA,
                quantidade = 1,
                motivo = "Saída manual • uso interno",
                dataHoraTimestamp = getTs(6, 10, 15),
                saldoAntes = 1,
                saldoDepois = 0
            )

            // V-004 (07 out. 16:20, Utilitário 500 kg, Agro Vale, -2 un., R$ 9.780,00, 10->8)
            val v4 = VendaEntity(
                id = 4,
                reboqueId = 4,
                clienteId = 2,
                quantidade = 2,
                valorUnitarioCents = 489000L,
                valorTotalCents = 978000L,
                dataHoraTimestamp = getTs(7, 16, 20),
                saldoAntes = 10,
                saldoDepois = 8
            )

            vendaDao.insertAll(listOf(v1, v2, v3, v4))
            compraDao.insertAll(listOf(cp1))
            ajusteDao.insertAll(listOf(a1))
        }
    }
}
