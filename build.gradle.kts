import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.springframework.boot") version "3.3.0-SNAPSHOT"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.graalvm.buildtools.native") version "0.10.0"
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
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

configurations {
    all {
        exclude("ch.qos.logback")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(kotlinx("datetime:0.6.0"))
    implementation(kotlinx("serialization-json:1.7.0-RC"))
    implementation(kotlinx("coroutines-core:1.8.1"))
    implementation(kotlinx("coroutines-reactor:1.8.1"))
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    // Logging
    implementation("io.klogging:klogging-spring-boot-starter:0.5.13")

    // Spring Boot Starter
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:neo4j")
    testImplementation("org.neo4j.driver:neo4j-java-driver:5.20.0")
    implementation(kotlinx("coroutines-test:1.8.1"))
}

tasks {
    test {
        useJUnitPlatform()
    }

    wrapper {
        version = "8.8-rc-1"
    }

    compileKotlin {
        compilerOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xcontext-receivers")
            jvmTarget = JvmTarget.JVM_22
        }
    }
}

fun kotlinx(module: String, version: String? = null) =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"
