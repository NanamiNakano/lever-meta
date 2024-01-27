package dev.thynanami.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeverMetaConfig(
    val database:DatabaseConfig = DatabaseConfig(),
    @SerialName("oss")
    val objectStorageConfig: ObjectStorageConfig = ObjectStorageConfig(),
)

@Serializable
data class DatabaseConfig(
    val type:String = "postgresql",
    val host:String = "localhost",
    val port:Int = 5432,
    val user:String = "",
    val password:String = "",
    val db:String = "postgres"
)

@Serializable
data class ObjectStorageConfig(
    val endpoint:String = "",
    val accessKey:String = "",
    val secretKey:String = "",
)
