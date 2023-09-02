plugins {
    id("org.springframework.boot") version "3.2.0-M2"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.spring") version "1.9.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlinx("datetime:0.4.0"))
    implementation(kotlinx("serialization-json:1.5.1"))
    implementation(kotlinx("coroutines-core:1.7.3"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation(kotlin("reflect"))
    implementation(kotlinx("coroutines-reactor"))
}

kotlin {
    jvmToolchain(20)
}

tasks {
    test {
        useJUnitPlatform()
    }

    wrapper {
        version = "8.3"
    }
}

fun kotlinx(module: String, version: String? = null): Any =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"
