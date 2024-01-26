package dev.thynanami

import dev.thynanami.dao.DatabaseSingleton
import dev.thynanami.ktor.plugins.configureRouting
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        DatabaseSingleton.init()
        configureRouting()
    }.start(wait = true)
}
