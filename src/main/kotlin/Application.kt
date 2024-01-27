package dev.thynanami

import dev.thynanami.dao.DatabaseSingleton
import dev.thynanami.ktor.plugins.configureRouting
import dev.thynanami.utils.Configurator
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("Lever Meta")

fun main() {
    embeddedServer(Netty, port = 8080) {
        Configurator.init()
        DatabaseSingleton.init()
        configureRouting()
    }.start(wait = true)
}
