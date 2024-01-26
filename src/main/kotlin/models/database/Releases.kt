package dev.thynanami.models.database

import dev.thynanami.models.ReleaseType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Releases: Table() {
    val id = varchar("id",16)
    val type = enumeration<ReleaseType>("type")
    val url = varchar("url", 256)
    val time = timestamp("time")
    val releaseTime = timestamp("releaseTime")
    val sha1 = varchar("sha1",40)
    val complianceLevel = integer("complianceLevel").default(0)

    override val primaryKey = PrimaryKey(id)
}
