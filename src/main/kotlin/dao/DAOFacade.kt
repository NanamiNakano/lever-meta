package dev.thynanami.dao

import dev.thynanami.models.GameRelease

interface DAOFacade {
    suspend fun allReleases():List<GameRelease>
    suspend fun addNewRelease(release: GameRelease):Boolean
    suspend fun updateRelease(release: GameRelease):Boolean
    suspend fun deleteRelease(id:String):Boolean
}
