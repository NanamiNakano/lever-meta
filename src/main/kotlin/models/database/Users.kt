package dev.thynanami.models.database

import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

data class User(
    val uuid: UUID,
    val username:String,
    val role:UserRole
)

enum class UserRole{
    ADMIN,
    MAINTAINER
}

object Users:UUIDTable() {
    val username = varchar("username", 32)
    val hashedPassword = varchar("password", 1024)
    val role = enumeration<UserRole>("role")
}
