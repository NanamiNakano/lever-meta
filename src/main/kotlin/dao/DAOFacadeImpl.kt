package dev.thynanami.dao

import dev.thynanami.dao.DatabaseSingleton.dbQuery
import dev.thynanami.logger
import dev.thynanami.models.GameRelease
import dev.thynanami.models.database.Releases
import dev.thynanami.models.database.User
import dev.thynanami.models.database.UserRole
import dev.thynanami.models.database.Users
import dev.thynanami.utils.generateSecurePassword
import dev.thynanami.utils.hashPassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class DAOFacadeImpl : DAOFacade {
    private fun resultToRelease(row: ResultRow): GameRelease = GameRelease(
        id = row[Releases.id],
        type = row[Releases.type],
        url = row[Releases.url],
        time = row[Releases.time],
        releaseTime = row[Releases.releaseTime],
        sha1 = row[Releases.sha1],
        complianceLevel = row[Releases.complianceLevel]
    )

    override suspend fun allReleases(): List<GameRelease> =
        dbQuery {
            Releases.selectAll().map(::resultToRelease)
        }


    override suspend fun addNewRelease(release: GameRelease): Boolean = dbQuery {
        val insertStatement = Releases.insert {
            it[id] = release.id
            it[url] = release.url
            it[releaseTime] = release.releaseTime
            it[sha1] = release.sha1
            it[time] = release.time
            it[complianceLevel] = release.complianceLevel
            it[type] = release.type
        }

        insertStatement.resultedValues?.singleOrNull() != null
    }

    override suspend fun updateRelease(release: GameRelease): Boolean = dbQuery {
        Releases.update({ Releases.id eq release.id }) {
            it[url] = release.url
            it[releaseTime] = release.releaseTime
            it[sha1] = release.sha1
            it[time] = release.time
            it[complianceLevel] = release.complianceLevel
            it[type] = release.type
        } > 0
    }

    override suspend fun deleteRelease(id: String): Boolean = dbQuery {
        Releases.deleteWhere { Releases.id eq id } > 0
    }

    private fun resultRowToUser(row: ResultRow): User = User(
        uuid = row[Users.id].value,
        username = row[Users.username],
        role = row[Users.role],
        hashedPassword = row[Users.hashedPassword]
    )

    override suspend fun addNewUser(username: String, hashedPassword: String, role: UserRole): User? = dbQuery {
        val insertStatement = Users.insert {
            it[Users.username] = username
            it[Users.hashedPassword] = hashedPassword
            it[Users.role] = role
        }

        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun updateUserRole(userUUID: UUID, newRole: UserRole): Boolean = dbQuery {
        Users.update({ Users.id eq userUUID }) {
            it[role] = newRole
        } > 0
    }


    override suspend fun deleteUser(userUUID: UUID): Boolean = dbQuery {
        Users.deleteWhere { Users.id eq userUUID } > 0
    }

    override suspend fun getUser(userUUID: UUID): User? = dbQuery {
        Users.selectAll().where { Users.id eq userUUID }.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun getUser(username: String): User? = dbQuery {
        Users.selectAll().where { Users.username eq username }.singleOrNull()?.let(::resultRowToUser)
    }
}

val dao = DAOFacadeImpl().apply {
    runBlocking {
        dbQuery {
            if (Users.selectAll().empty()) {
                val randomPassword = generateSecurePassword(16)
                addNewUser("admin", hashPassword(randomPassword), UserRole.ADMIN)
                logger.info("Add new admin account\nUsername: admin\nPassword: $randomPassword")
            }
        }
    }
}
