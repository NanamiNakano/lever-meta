package dev.thynanami.utils

import java.util.*



object SystemInfo {
    var osName = System.getProperty("os.name")
    var osVersion = System.getProperty("os.version").lowercase(Locale.ENGLISH)

    init {
        if (osName.startsWith("Windows") && osName.matches(Regex.fromLiteral("Windows \\d+"))) {
            // for whatever reason, JRE reports "Windows 11" as a name and "10.0" as a version on Windows 11
            run {
                val version2 = osName.substring("Windows".length + 1) + ".0"
                if (version2.toFloat() > osVersion.toFloat()) {
                    osVersion = version2
                }
            }
            osName = "Windows"
        }
    }

    val runtimeVersion = Runtime.version()
}
