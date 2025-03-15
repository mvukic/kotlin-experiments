import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.springframework.boot") version "3.4.0-SNAPSHOT"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.graalvm.buildtools.native") version "0.10.2"
    kotlin("jvm") version "2.1.0-Beta2"
    kotlin("plugin.spring") version "2.1.0-Beta2"
    kotlin("plugin.serialization") version "2.1.0-Beta2"
}

group = "org.mvukic"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(22)
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

configurations.all {
    exclude("ch.qos.logback")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(kotlinx("datetime:0.6.1"))
    implementation(kotlinx("serialization-json:1.7.3"))
    implementation(kotlinx("coroutines-core:1.9.0"))
    implementation(kotlinx("coroutines-reactor:1.9.0"))
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    // Logging
    implementation("io.klogging:klogging-spring-boot-starter:0.7.2")

    // Spring Boot Starter
    implementation("org.springframework.boot:spring-boot-starter-webflux") {
        exclude(module = "spring-boot-starter-json")
    }

    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Database
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")

    implementation(kotlinx("coroutines-test:1.9.0"))
}

tasks {
    test {
        useJUnitPlatform()
    }

    wrapper {
        version = "8.11-rc-1"
    }

    compileKotlin {
        compilerOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xwhen-guards")
            jvmTarget = JvmTarget.JVM_22
        }
    }
}

fun kotlinx(module: String, version: String? = null) =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"
