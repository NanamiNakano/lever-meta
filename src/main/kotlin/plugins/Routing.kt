package dev.thynanami.plugins

import dev.thynanami.APP_VERSION
import dev.thynanami.dao.dao
import dev.thynanami.models.GameReleaseClassic
import dev.thynanami.models.Latest
import dev.thynanami.models.ReleaseManifest
import dev.thynanami.models.ReleaseManifestV2
import dev.thynanami.utils.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get
import io.ktor.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(
                "Lever meta server version $APP_VERSION\n" + "Running on ${SystemUtils.osName} ${SystemUtils.osVersion}\n" +
                        "Runtime: JRE ${SystemUtils.runtimeVersion}"
            )
        }

        get("/version_manifest.json") {
            val releaseList = dao.allReleases()
            val manifest =
                ReleaseManifest(
                    Latest(release = releaseList.latestRelease(), snapshot = releaseList.latestSnapshot()),
                    releaseList.map {
                        GameReleaseClassic(
                            id = it.id,
                            type = it.type,
                            url = it.url,
                            time = it.time,
                            releaseTime = it.releaseTime
                        )
                    }
                )
            call.respondText(Json.encodeToString(manifest), ContentType.Application.Json)
        }

        get("/version_manifest_v2.json") {
            val releaseList = dao.allReleases()
            val manifest =
                ReleaseManifestV2(
                    Latest(release = releaseList.latestRelease(), snapshot = releaseList.latestSnapshot()),
                    releaseList
                )
            call.respondText(Json.encodeToString(manifest), ContentType.Application.Json)
        }

        route("/api") {
            post("/auth") {
                val formParameters = call.receiveParameters()
                val username = formParameters["username"].toString()
                val plainTextPassword = formParameters["password"].toString()
                val user = dao.getUser(username)
                if (user == null || !verifyPassword(plainTextPassword, user.hashedPassword)) {
                    call.respond(HttpStatusCode.BadRequest, "username or password incorrect")
                } else {
                    val token = generateToken()
                    saveToken(user.uuid, token)
                    call.respond(HttpStatusCode.OK, token)
                }
            }

            authenticate("bearer") {
                route("/users") {
                    get("/info") {
                        val username = call.principal<UserIdPrincipal>()?.name
                        val user = username?.let { it1 -> dao.getUser(it1) }
                        println(user)
                        if (user == null) {
                            call.respond(HttpStatusCode.InternalServerError)
                        } else {
                            call.respond(user)
                        }
                    }
                }
            }
        }
    }
}
