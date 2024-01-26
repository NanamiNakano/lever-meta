package dev.thynanami.ktor.plugins

import dev.thynanami.APP_VERSION
import dev.thynanami.dao.dao
import dev.thynanami.models.GameReleaseClassic
import dev.thynanami.models.Latest
import dev.thynanami.models.ReleaseManifest
import dev.thynanami.models.ReleaseManifestV2
import dev.thynanami.utils.SystemInfo
import dev.thynanami.utils.latestRelease
import dev.thynanami.utils.latestSnapshot
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(
                "Lever meta server version $APP_VERSION\n" + "Running on ${SystemInfo.osName} ${SystemInfo.osVersion}\n" +
                        "Runtime: JRE ${SystemInfo.runtimeVersion}"
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
    }
}
