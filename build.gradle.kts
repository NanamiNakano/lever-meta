import com.github.gmazzo.buildconfig.BuildConfigSourceSet
import java.io.FileOutputStream
import java.util.*

val exposedVersion: String by project
val ktorVersion: String by project
val logbackVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "2.3.7"
    kotlin("plugin.serialization") version "1.9.22"
    id("com.github.gmazzo.buildconfig") version "5.3.5"
}

group = "dev.thynanami"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-crypt:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-json:$exposedVersion")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    implementation("org.postgresql:postgresql:42.7.1")
}

application {
    mainClass.set("dev.thynanami.lever-meta.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")

}

tasks.test {
    useJUnitPlatform()
}

buildConfig {
    packageName("dev.thynanami")
    buildConfigField("APP_VERSION", version.toString())
    useKotlinOutput { topLevelConstants = true }
}
kotlin {
    jvmToolchain(21)
}

fun BuildConfigSourceSet.string(name: String, value: String) = buildConfigField("String", name, "\"$value\"")
fun BuildConfigSourceSet.stringNullable(name: String, value: String?) =
    buildConfigField("String?", name, value?.let { "\"$value\"" } ?: "null")
