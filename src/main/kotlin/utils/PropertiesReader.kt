package dev.thynanami.utils

import java.nio.file.Path
import java.util.Properties
import kotlin.io.path.pathString

class PropertiesReader(property: Path) {
    private val properties = Properties()

    init {
        val dataStream = this.javaClass.classLoader.getResourceAsStream(property.pathString)
            ?: throw NoSuchFileException(property.toFile())
        properties.load(dataStream)
    }

    fun getProperty(key:String):String = properties.getProperty(key)
}
