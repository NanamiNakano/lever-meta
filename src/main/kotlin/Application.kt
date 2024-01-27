package dev.thynanami

import dev.thynanami.plugins.configureAuthorization
import dev.thynanami.plugins.configureRouting
import dev.thynanami.utils.Configurator
import dev.thynanami.utils.appConfig
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.security.KeyStore
import kotlin.io.path.Path
import kotlin.io.path.inputStream

val logger: Logger = LoggerFactory.getLogger("Lever Meta")
val keyStore: KeyStore by lazy {
    KeyStore.getInstance("JKS")
}

fun main() {
    Configurator.init()

    val environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
        connector {
            port = 8080
        }
        if (appConfig.sslConfig.enabled) {
            keyStore.load(Path(appConfig.sslConfig.keyStore).inputStream(), appConfig.sslConfig.keyStorePassword.toCharArray())
            sslConnector(
                keyStore = keyStore,
                keyAlias = appConfig.sslConfig.keyStoreAlias,
                keyStorePassword = { appConfig.sslConfig.keyStorePassword.toCharArray()},
                privateKeyPassword = { appConfig.sslConfig.privateKeyPassword.toCharArray()}
            ) {
                port = appConfig.sslConfig.sslPort
                keyStorePath = Path(appConfig.sslConfig.keyStore).toFile()
            }
        }
        module {
            configureAuthorization()
            configureRouting()
        }
    }

    embeddedServer(Netty, environment).start(wait = true)
}
