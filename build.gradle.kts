import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.springframework.boot") version "3.2.0-RC2"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "2.0.0-Beta1"
    kotlin("plugin.spring") version "2.0.0-Beta1"
    kotlin("plugin.serialization") version "2.0.0-Beta1"
}

group = "org.mvukic"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
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
    implementation(kotlinx("serialization-json:1.6.1"))
    implementation(kotlinx("coroutines-core:1.7.3"))
    implementation(kotlinx("coroutines-reactor:1.7.3"))

    implementation("io.klogging:klogging-spring-boot-starter:0.5.6")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
}

tasks {
    test {
        useJUnitPlatform()
    }

    wrapper {
        version = "8.5-rc-2"
    }

    compileKotlin {
        compilerOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = JvmTarget.JVM_21
        }
    }
}

fun kotlinx(module: String, version: String? = null) =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"
