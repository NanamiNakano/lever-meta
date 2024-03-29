package dev.thynanami.plugins.ktor

import dev.thynanami.APP_VERSION
import dev.thynanami.TENON_VERSION
import dev.thynanami.dao.dao
import dev.thynanami.models.*
import dev.thynanami.models.database.UserRole
import dev.thynanami.plugins.checkHealth
import dev.thynanami.plugins.tenonClient
import dev.thynanami.utils.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(
                "Lever meta server version $APP_VERSION\n" +
                        "Tenon version $TENON_VERSION\n" +
                        "Running on ${SystemUtils.osName} ${SystemUtils.osVersion}\n" +
                        "Runtime: JRE ${SystemUtils.runtimeVersion}"
            )
        }

        get("/health-check") {
            val health = checkHealth()
            if (health.filter { !it.value }.isNotEmpty()) {
                call.respond(HttpStatusCode.InternalServerError, health)
            } else {
                call.respond(HttpStatusCode.OK, health)
            }
        }

        route("/mc/game") {
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
        }

        route("/v1") {
            route("/packages") {
                get("/{sha1}/{id}.json") {
                    val sha1 = call.parameters["sha1"]
                    val id = call.parameters["id"]
                    if (sha1 == null || id == null) {
                        return@get call.respond(HttpStatusCode.NotFound)
                    }
                    dao.getRelease(id) ?: return@get call.respond(HttpStatusCode.NotFound)
                    call.respondOutputStream { tenonClient.serve("/packages/$id.json", this) }
                }
            }

            route("objects") {
                get("/{sha1}/{file}") {
                    val sha1 = call.parameters["sha1"]
                    val file = call.parameters["file"]
                    if (sha1 == null || file == null) {
                        return@get call.respond(HttpStatusCode.NotFound)
                    }
                    call.respondOutputStream { tenonClient.serve("/objects/$file", this) }
                }
            }
        }

        route("/api") {
            post("/auth") {
                val formParameters = call.receiveParameters()
                val username = formParameters["username"].toString()
                val plainTextPassword = formParameters["password"].toString()
                val user = dao.getUser(username)
                if (user == null || !verifyPassword(plainTextPassword, user.hashedPassword)) {
                    return@post call.respond(HttpStatusCode.BadRequest, "username or password incorrect")
                }
                val token = generateToken()
                if (saveToken(user.uuid, token)) {
                    call.respond(HttpStatusCode.OK, token)
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            authenticate("bearer") {
                route("/users") {
                    get("/info") {
                        val username = call.principal<UserIdPrincipal>()?.name
                        val user = username?.let { it1 -> dao.getUser(it1) }
                        if (user == null) {
                            return@get call.respond(HttpStatusCode.InternalServerError)
                        }
                        call.respond(user.toUserInfo())
                    }

                    post("/new") {
                        val user = call.principal<UserIdPrincipal>()?.name
                            ?: return@post call.respond(HttpStatusCode.InternalServerError)
                        val userRole =
                            dao.getUser(user)?.role ?: return@post call.respond(HttpStatusCode.InternalServerError)
                        if (userRole != UserRole.ADMIN) {
                            return@post call.respond(HttpStatusCode.Unauthorized, "You are not admin!")
                        }
                        val formParameters = call.receiveParameters()
                        val username = formParameters["username"].toString()
                        val plainTextPassword = formParameters["password"].toString()
                        val hashedPassword = hashPassword(plainTextPassword)
                        val role = when (formParameters["role"].toString()) {
                            "admin" -> UserRole.ADMIN
                            "maintainer" -> UserRole.MAINTAINER
                            else -> return@post call.respond(HttpStatusCode.NotAcceptable, "Invalid user role.")
                        }
                        val newUserInfo =
                            dao.addNewUser(username, hashedPassword, role)?.toUserInfo()
                                ?: return@post call.respond(
                                    HttpStatusCode.InternalServerError,
                                    "Unable to add new user."
                                )
                        call.respond(HttpStatusCode.OK, newUserInfo)
                    }

                    get("/delete") {
                        val uuid = call.request.queryParameters["uuid"]
                            ?: return@get call.respond(HttpStatusCode.NotAcceptable)
                        val user = call.principal<UserIdPrincipal>()?.name
                            ?: return@get call.respond(HttpStatusCode.InternalServerError)
                        val userRole =
                            dao.getUser(user)?.role ?: return@get call.respond(HttpStatusCode.InternalServerError)
                        if (userRole != UserRole.ADMIN) {
                            return@get call.respond(HttpStatusCode.Unauthorized, "You are not admin!")
                        }
                        val success = dao.deleteUser(UUID.fromString(uuid))
                        if (!success) {
                            return@get call.respond(HttpStatusCode.InternalServerError, "Unable to delete user")
                        }
                        call.respond(HttpStatusCode.OK, "Success!")
                    }
                }

                route("files") {
                    post("/upload") {
                        val contentType = call.request.headers["Content-Type"] ?: return@post call.respond(
                            HttpStatusCode.NotAcceptable
                        )
                        val contentLength =
                            call.request.contentLength() ?: return@post call.respond(HttpStatusCode.NotAcceptable)
                        val name = call.request.headers["Object-Name"]
                            ?: return@post call.respond(HttpStatusCode.NotAcceptable)
                        val inputStream = call.receiveStream()
                        if (tenonClient.upload(inputStream, name, contentType, contentLength)) {
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.InternalServerError)
                        }
                    }

                    get("/delete") {
                        val target = call.request.queryParameters["objectName"] ?: return@get call.respond(
                            HttpStatusCode.NotAcceptable
                        )
                        if (tenonClient.delete(target)) {
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.InternalServerError)
                        }
                    }
                }

                route("/releases") {
                    post("/add") {
                        val release = call.receive<GameRelease>()
                        if (dao.addNewRelease(release)) {
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.InternalServerError)
                        }
                    }

                    get("/delete") {
                        val id =
                            call.request.queryParameters["id"] ?: return@get call.respond(HttpStatusCode.NotAcceptable)
                        if (dao.deleteRelease(id)) {
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.InternalServerError)
                        }
                    }

                    post("/update") {
                        val release = call.receive<GameRelease>()
                        if (dao.updateRelease(release)) {
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.InternalServerError)
                        }
                    }
                }
            }
        }
    }
}
