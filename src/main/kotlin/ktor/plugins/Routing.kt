package dev.thynanami.ktor.plugins

import dev.thynanami.APP_VERSION
import dev.thynanami.utils.SystemInfo
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(
                "Lever meta server version $APP_VERSION\n" + "Running on ${SystemInfo.osName} ${SystemInfo.osVersion}\n" +
                        "Runtime: JRE ${SystemInfo.runtimeVersion}"
            )
        }


    }
}
