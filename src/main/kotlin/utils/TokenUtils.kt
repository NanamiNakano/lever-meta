package dev.thynanami.utils

import io.github.crackthecodeabhi.kreds.connection.Endpoint
import io.github.crackthecodeabhi.kreds.connection.newClient
import java.util.UUID

private val tokenCharSet = ('A'..'Z') + ('a'..'z') + ('0'..'9') + listOf('.', '_', '~', '+', '/', '-')

fun generateToken(): String {
    return List(256) {
        tokenCharSet.random()
    }.joinToString("")
}

private val redisClient by lazy {
    val client = newClient(
        Endpoint(
            appConfig.redisConfig.host,
            appConfig.redisConfig.port
        )
    )
    closeOnExit {
        client.close()
    }
    client
}

suspend fun saveToken(userUUID: UUID, token: String, expire: ULong = 2592000u) { // expire after 30 days by default
    redisClient.set(token, userUUID.toString())
    if (expire > 0u) {
        redisClient.expire(token, expire)
    }
}

suspend fun checkToken(token: String): UUID? {
    return redisClient.get(token)?.let { UUID.fromString(it) }
}
