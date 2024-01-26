package dev.thynanami.dao

import dev.thynanami.models.database.Releases
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseSingleton {
    private lateinit var database: Database
    fun init() {
        val driverName = "org.postgresql.Driver"
        val jdbcUrl = "jdbc:postgresql://localhost:5432/postgres"
        database = Database.connect(jdbcUrl, driverName,"postgres","mysecretpassword")
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(Releases)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }
}
