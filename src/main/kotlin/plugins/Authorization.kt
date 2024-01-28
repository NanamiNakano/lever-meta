package dev.thynanami.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureAuthorization() {
    authentication {
        bearer("bearer") {
            realm = "Access to lever meta api"
        }
    }
}
