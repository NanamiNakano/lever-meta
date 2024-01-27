package dev.thynanami.dao

import dev.thynanami.models.GameRelease
import dev.thynanami.models.database.User
import dev.thynanami.models.database.UserRole
import java.util.UUID

interface DAOFacade {
    suspend fun allReleases():List<GameRelease>
    suspend fun addNewRelease(release: GameRelease):Boolean
    suspend fun updateRelease(release: GameRelease):Boolean
    suspend fun deleteRelease(id:String):Boolean

    suspend fun addNewUser(username:String,hashedPassword:String,role: UserRole):User?
    suspend fun updateUserRole(userUUID: UUID, newRole: UserRole):Boolean
    suspend fun deleteUser(userUUID: UUID):Boolean
    suspend fun getUser(userUUID: UUID):User?
}
