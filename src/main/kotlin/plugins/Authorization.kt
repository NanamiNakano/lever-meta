package dev.thynanami.plugins

import dev.thynanami.dao.dao
import dev.thynanami.utils.checkToken
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*

fun Application.configureAuthorization() {
    authentication {
        bearer("bearer") {
            realm = "Access to lever meta api"
            authenticate { bearerTokenCredential ->
                val userUUID = checkToken(bearerTokenCredential.token)
                if (userUUID == null) {
                    null
                } else {
                    val username = dao.getUser(userUUID)?.username
                    if (username == null) {
                        respond(HttpStatusCode.InternalServerError)
                        null
                    } else {
                        UserIdPrincipal(username)
                    }
                }
            }
        }
    }
}
