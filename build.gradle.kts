import com.github.gmazzo.buildconfig.BuildConfigSourceSet

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
    implementation("com.charleskorn.kaml:kaml:0.57.0")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-crypt:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-json:$exposedVersion")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    implementation("org.postgresql:postgresql:42.7.1")
    implementation("io.github.crackthecodeabhi:kreds:0.9.1")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("io.github.crackthecodeabhi:kreds:0.9.1")
    implementation("com.password4j:password4j:1.7.3")
}

application {
    mainClass.set("dev.thynanami.lever-meta.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")

}

tasks.test {
    useJUnitPlatform()
}

task<Exec>("generateCertificate") {
    commandLine(
        "keytool",
        "-keystore",
        "keystore.jks",
        "-storepass",
        "development",
        "-keypass",
        "development",
        "-alias",
        "development",
        "-genkeypair",
        "-keyalg",
        "RSA",
        "-keysize",
        "4096",
        "-validity",
        "3",
        "-dname",
        "CN=localhost,OU=ktor,O=ktor,L=Unspecified,ST=Unspecified,C=US"
    )
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
