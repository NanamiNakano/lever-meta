package dev.thynanami.utils

import com.charleskorn.kaml.Yaml
import dev.thynanami.dao.DatabaseSingleton
import dev.thynanami.models.LeverMetaConfig
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.concurrent.thread
import kotlin.io.path.*

lateinit var config: LeverMetaConfig

object Configurator {
    private val pathToYaml =
        Path(System.getProperty("leverMetaConfig") ?: System.getenv("LEVER_META_CONFIG") ?: "./config.yaml")

    fun init() {
        if (!pathToYaml.exists()) {
            pathToYaml.createFile()
            saveConfig(LeverMetaConfig())
        }
        config = loadConfig()
        DatabaseSingleton.init()

        Runtime.getRuntime().addShutdownHook(Thread {
            saveConfig(config)
        })
    }

    private fun loadConfig(): LeverMetaConfig {
        val rawData = pathToYaml.readText()
        return Yaml.default.decodeFromString(rawData)
    }

    private fun saveConfig(config: LeverMetaConfig) {
        val rawData = Yaml.default.encodeToString(config)
        pathToYaml.writeText(rawData)
    }
}
