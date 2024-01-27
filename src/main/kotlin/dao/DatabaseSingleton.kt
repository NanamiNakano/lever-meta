package dev.thynanami.dao

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.pool.HikariPool
import dev.thynanami.logger
import dev.thynanami.models.database.Releases
import dev.thynanami.utils.config
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.system.exitProcess

object DatabaseSingleton {
    private lateinit var database: Database
    fun init() {
        try {
            val config = HikariConfig().apply {
                jdbcUrl = JDBCUrl
                driverClassName = driverClass
                username = config.database.user
                password = config.database.password
                maximumPoolSize = 6
                isReadOnly = false
                transactionIsolation = "TRANSACTION_SERIALIZABLE"
            }
            val dataSource = HikariDataSource(config)
            database = Database.connect(datasource = dataSource)
            transaction(database) {
                SchemaUtils.createMissingTablesAndColumns(Releases)
            }
        } catch (ex: HikariPool.PoolInitializationException) {
            logger.error("Invalid database configuration in config file.")
            exitProcess(10)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }

    private val JDBCUrl by lazy {
        if (config.database.type == "sqlite" || config.database.type == "h2") {
            "jdbc:${config.database.type}:${config.database.db}"
        }
        "jdbc:${config.database.type}://${config.database.host}:${config.database.port}/${config.database.db}"
    }


    private val driverMapping = mutableMapOf(
        "jdbc:h2" to "org.h2.Driver",
        "jdbc:postgresql" to "org.postgresql.Driver",
        "jdbc:mysql" to "com.mysql.cj.jdbc.Driver",
        "jdbc:mariadb" to "org.mariadb.jdbc.Driver",
        "jdbc:sqlite" to "org.sqlite.JDBC",
    )


    val driverClass by lazy {
        driverMapping.entries.firstOrNull { (prefix, _) ->
            JDBCUrl.startsWith(prefix)
        }?.value ?: error("Database driver not found for $JDBCUrl")
    }
}
