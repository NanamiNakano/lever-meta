package dev.thynanami.utils

import com.charleskorn.kaml.Yaml
import dev.thynanami.dao.DatabaseSingleton
import dev.thynanami.models.LeverMetaConfig
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.io.path.*

lateinit var appConfig: LeverMetaConfig

object Configurator {
    private val pathToYaml =
        Path(System.getProperty("leverMetaConfig") ?: System.getenv("LEVER_META_CONFIG") ?: "./config.yaml")

    fun init() {
        if (!pathToYaml.exists()) {
            pathToYaml.createFile()
            appConfig = LeverMetaConfig()
        }
        appConfig = loadConfig()
        DatabaseSingleton.init()

        Runtime.getRuntime().addShutdownHook(Thread {
            saveConfig(appConfig)
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
