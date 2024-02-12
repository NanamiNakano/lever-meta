package dev.thynanami.plugins

import dev.thynanami.dao.dao
import dev.thynanami.utils.redisClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.io.path.*

suspend fun checkHealth():Map<String,Boolean> {
    val tenonHealth = runCatching {
        withContext(Dispatchers.IO) {
            val file = createTempFile()
            tenonClient.upload(file)
            tenonClient.delete(file.name)
            file.deleteIfExists()
        }
    }.isSuccess

    val psqlHealth = runCatching {
        dao.allReleases()
    }.isSuccess

    val redisHealth = runCatching {
        redisClient.echo("Health Check")
    }.isSuccess

    return mapOf("Tenon" to tenonHealth,"Postgres" to psqlHealth, "Redis" to redisHealth)
}
