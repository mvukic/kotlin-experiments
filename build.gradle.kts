plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
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
    implementation(kotlinx("coroutines-core:1.7.2"))
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(20)
}

tasks {
    test {
        useJUnitPlatform()
    }

    wrapper {
        version = "8.2"
    }
}

fun kotlinx(module: String, version: String? = null): Any =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"
