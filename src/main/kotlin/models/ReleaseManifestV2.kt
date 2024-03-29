package dev.thynanami.models

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReleaseManifestV2(
    val latest: Latest,
    @SerialName("versions")
    val gameReleases: List<GameRelease>,
)

@Serializable
data class GameRelease(
    val id: String,
    val type: ReleaseType,
    val url: String,
    val time: Instant,
    val releaseTime: Instant,
    val sha1: String,
    val complianceLevel: Int,
)

@Serializable
data class Latest(
    val release: String,
    val snapshot: String,
)

@Serializable
enum class ReleaseType {
    @SerialName("release")
    RELEASE,
    @SerialName("snapshot")
    SNAPSHOT,
}

@Serializable
data class ReleaseManifest(
    val latest: Latest,
    @SerialName("versions")
    val gameReleases: List<GameReleaseClassic>,
)

@Serializable
data class GameReleaseClassic(
    val id: String,
    val type: ReleaseType,
    val url: String,
    val time: Instant,
    val releaseTime: Instant,
)
