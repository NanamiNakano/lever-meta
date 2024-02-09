package dev.thynanami.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeverMetaConfig(
    val upstream: List<Upstream> = listOf(Upstream("https://piston-meta.mojang.com", false)),
    @SerialName("postgres")
    val postgresConfig: PostgresConfig = PostgresConfig(),
    @SerialName("oss")
    val objectStorageConfig: ObjectStorageConfig = ObjectStorageConfig(),
    @SerialName("ssl")
    val sslConfig: SSLConfig = SSLConfig(),
    @SerialName("redis")
    val redisConfig: RedisConfig = RedisConfig(),
    @SerialName("s3")
    val s3Config: S3Config = S3Config(),
)

@Serializable
data class Upstream(
    val url: String,
    val show: Boolean,
)

@Serializable
data class PostgresConfig(
    val host: String = "localhost",
    val port: Int = 5432,
    val user: String = "",
    val password: String = "",
    val db: String = "postgres",
)

@Serializable
data class ObjectStorageConfig(
    val endpoint: String = "",
    val accessKey: String = "",
    val secretKey: String = "",
)

@Serializable
data class SSLConfig(
    val enabled: Boolean = false,
    val sslPort: Int = 8443,
    val keyStore: String = "",
    val keyStoreAlias: String = "",
    val keyStorePassword: String = "",
    val privateKeyPassword: String = "",
)

@Serializable
data class RedisConfig(
    val host: String = "localhost",
    val port: Int = 6379,
)

@Serializable
data class S3Config(
    val endpoint: String = "",
    val accessKey: String = "",
    val secretKey: String = "",
    val bucket: String = "",
)

@Serializable
data class TenonConfig(
    @SerialName("s3")
    val s3Config: S3Config = S3Config(),
    val copy: Boolean = false,
)
