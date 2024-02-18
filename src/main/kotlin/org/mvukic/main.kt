package org.mvukic

import io.klogging.Level
import io.klogging.config.loggingConfiguration
import io.klogging.context.Context
import io.klogging.events.LogEvent
import io.klogging.rendering.*
import io.klogging.sending.STDOUT
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import org.jetbrains.annotations.BlockingExecutor
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import java.util.concurrent.Executors


// https://github.com/sdeleuze/spring-framework/commits?author=sdeleuze
@SpringBootApplication
@EnableCaching
class SpringBootApp

fun main(args: Array<String>) {
    configureLogging()
    runApplication<SpringBootApp>(*args)
}

private fun configureLogging() {
    val renderCustom: RenderString = object : RenderString {
        override fun invoke(event: LogEvent): String {
            val eventMap: MutableMap<String, Any?> =
                (mapOf("@t" to event.timestamp.toString()) + event.items).toMutableMap()
            if (event.template != null) eventMap["@mt"] = event.template else eventMap["@m"] = event.message
            return serializeMap(eventMap)
        }
    }

    val env = getEnv()
    loggingConfiguration {
        sink("local", RENDER_ANSI, STDOUT)
        sink("dev", RENDER_ISO8601, STDOUT)
        sink("test", renderCustom, STDOUT)
        sink("stage", RENDER_ECS, STDOUT)
        sink("prod", RENDER_GELF, STDOUT)
        logging {
            fromMinLevel(Level.INFO) {
                toSink(env)
            }
        }
    }
    Context.addBaseContext(
        "app" to "LoggingDemo",
        "version" to "1.2.0",
        "env" to env
    )
}

private fun getEnv(): String {
    val envs = listOf("local", "dev", "test", "stage", "prod")
    val env = System.getProperty("spring.profiles.active", "local")
    return if (env in envs) env else error("Unknown environment: $env")
}

val Dispatchers.LOOM: @BlockingExecutor CoroutineDispatcher
    get() = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
