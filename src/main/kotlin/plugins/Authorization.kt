package dev.thynanami.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import kotlin.collections.set

fun Application.configureAuthorization() {
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.extensions["SameSite"] = "lax"
        }
    }
}

data class UserSession(
    val device: String,
    val username: String,
    val sessionId: String,
)
