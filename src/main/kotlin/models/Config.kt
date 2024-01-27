package dev.thynanami.models

import kotlinx.serialization.Serializable

@Serializable
data class LeverMetaConfig(
    val database:DatabaseConfig = DatabaseConfig()
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
