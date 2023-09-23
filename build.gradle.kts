import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.springframework.boot") version "3.2.0-SNAPSHOT"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.9.20-Beta2"
    kotlin("plugin.spring") version "1.9.20-Beta2"
    kotlin("plugin.serialization") version "1.9.20-Beta2"
}

group = "org.mvukic"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(21)
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
    implementation(kotlinx("datetime:0.4.1"))
    implementation(kotlinx("serialization-json:1.6.0"))
    implementation(kotlinx("coroutines-core:1.7.3"))
    implementation(kotlinx("coroutines-reactor:1.7.3"))

    implementation("io.klogging:klogging-spring-boot-starter:0.5.6")
//    implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
}

tasks {
    test {
        useJUnitPlatform()
    }

    wrapper {
        version = "8.4-rc-1"
    }

    compileKotlin {
        compilerOptions {
            freeCompilerArgs.set(listOf("-Xjsr305=strict"))
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
}

fun kotlinx(module: String, version: String? = null): Any =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"
