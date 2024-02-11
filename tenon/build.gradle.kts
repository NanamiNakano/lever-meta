plugins {
    kotlin("jvm")
}

group = "dev.thynanami.tenon"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.minio:minio:8.5.7")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
