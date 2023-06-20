plugins {
    kotlin("jvm") version "1.9.0-RC"
    kotlin("plugin.serialization") version "1.8.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlinx("datetime:0.4.0"))
    implementation(kotlinx("serialization-json:1.5.1"))
    implementation(kotlinx("coroutines-core:1.7.1"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(20)
}

tasks {
    wrapper {
        version = "8.1.1"
    }
}

fun DependencyHandler.kotlinx(module: String, version: String? = null): Any =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"
