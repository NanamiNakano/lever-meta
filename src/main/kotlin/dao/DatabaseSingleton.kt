package dev.thynanami.dao

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.pool.HikariPool
import dev.thynanami.logger
import dev.thynanami.models.database.Releases
import dev.thynanami.models.database.Users
import dev.thynanami.utils.appConfig
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
                driverClassName = "org.postgresql.Driver"
                username = appConfig.postgresConfig.user
                password = appConfig.postgresConfig.password
                maximumPoolSize = 6
                isReadOnly = false
            }
            val dataSource = HikariDataSource(config)
            database = Database.connect(datasource = dataSource)
            transaction(database) {
                SchemaUtils.createMissingTablesAndColumns(Releases)
                SchemaUtils.createMissingTablesAndColumns(Users)
            }
        } catch (ex: HikariPool.PoolInitializationException) {
            logger.error("Invalid database configuration in config file or database it not running.")
            exitProcess(10)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }

    private val JDBCUrl =
        "jdbc:postgresql://${appConfig.postgresConfig.host}:${appConfig.postgresConfig.port}/${appConfig.postgresConfig.db}"

}
