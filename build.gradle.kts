import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.springframework.boot") version "3.2.0-SNAPSHOT"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.9.20-Beta"
    kotlin("plugin.spring") version "1.9.20-Beta"
}

group = "org.example"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(20)
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
    implementation(kotlinx("datetime:0.4.0"))
    implementation(kotlinx("serialization-json:1.6.0"))
    implementation(kotlinx("coroutines-core"))
    implementation(kotlinx("coroutines-reactor"))

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
        version = "8.3"
    }

    compileKotlin {
        compilerOptions {
            freeCompilerArgs.set(listOf("-Xjsr305=strict"))
            jvmTarget.set(JvmTarget.JVM_20)
        }
    }
}

fun kotlinx(module: String, version: String? = null): Any =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"
