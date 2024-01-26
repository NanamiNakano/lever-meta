package dev.thynanami.dao

import dev.thynanami.dao.DatabaseSingleton.dbQuery
import dev.thynanami.models.GameRelease
import dev.thynanami.models.ReleaseType
import dev.thynanami.models.database.Releases
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

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
            it[Releases.id] = release.id
            it[Releases.url] = release.url
            it[Releases.releaseTime] = release.releaseTime
            it[Releases.sha1] = release.sha1
            it[Releases.time] = release.time
            it[Releases.complianceLevel] = release.complianceLevel
            it[Releases.type] = release.type
        }

        insertStatement.resultedValues?.singleOrNull() != null
    }

    override suspend fun updateRelease(release: GameRelease): Boolean = dbQuery {
        Releases.update({ Releases.id eq release.id }) {
            it[Releases.url] = release.url
            it[Releases.releaseTime] = release.releaseTime
            it[Releases.sha1] = release.sha1
            it[Releases.time] = release.time
            it[Releases.complianceLevel] = release.complianceLevel
            it[Releases.type] = release.type
        } > 0
    }

    override suspend fun deleteRelease(id: String): Boolean = dbQuery {
        Releases.deleteWhere { Releases.id eq id } > 0
    }
}

val dao by lazy {
    DAOFacadeImpl()
}
