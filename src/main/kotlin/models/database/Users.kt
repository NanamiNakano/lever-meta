package dev.thynanami.models.database

import dev.thynanami.utils.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

@Serializable
data class User(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val username: String,
    val hashedPassword: String,
    val role: UserRole,
) {
    fun toUserInfo():UserInfo = UserInfo(uuid,username,role)
}

@Serializable
data class UserInfo(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val username: String,
    val role: UserRole,
)

@Serializable
enum class UserRole {
    @SerialName("admin")
    ADMIN,
    @SerialName("maintainer")
    MAINTAINER
}

object Users : UUIDTable() {
    val username = varchar("username", 32)
    val hashedPassword = varchar("password", 1024)
    val role = enumeration<UserRole>("role")
}
