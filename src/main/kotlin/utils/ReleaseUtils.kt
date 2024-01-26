package dev.thynanami.utils

import dev.thynanami.models.GameRelease
import dev.thynanami.models.ReleaseType

fun List<GameRelease>.latestRelease(): String {
    return this.filter { it.type == ReleaseType.RELEASE }.maxByOrNull { it.releaseTime }?.id ?: ""
}

fun List<GameRelease>.latestSnapshot(): String {
    return this.filter { it.type == ReleaseType.SNAPSHOT }.maxByOrNull { it.releaseTime }?.id ?: ""
}
