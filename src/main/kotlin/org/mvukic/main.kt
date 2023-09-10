package org.mvukic

import io.klogging.Klogging
import io.klogging.context.Context
import io.klogging.context.withLogContext
import kotlinx.coroutines.reactor.ReactorContext
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import kotlin.coroutines.coroutineContext


@Service
class RouterHandler : Klogging {
    suspend fun get(request: ServerRequest): ServerResponse {
        logger.info("test")
        return ServerResponse.ok().buildAndAwait()
    }
}

@Configuration
class RouterClass(private val handler: RouterHandler) {

    @Bean
    fun routerFn() = coRouter {
        GET("get", handler::get)
    }
}

@SpringBootApplication
class SpringBootApp

fun main(args: Array<String>) {
    // Always logged
    Context.addBaseContext(
        "app" to "LoggingDemo",
        "version" to "1.2.0"
    )

    // Always logged but dynamically fetched from the ReactorContext
    Context.addContextItemExtractor(ReactorContext) { ctx ->
        // Get request id
        val requestId = ctx.context.getOrDefault<String>("requestId", null) ?: "Not found"
        // Get user (optional)
        val user = ctx.context.getOrDefault<User>("user", null)
        mapOf("requestId" to requestId, "user" to (user?.name ?: "not found"))
    }

    runApplication<SpringBootApp>(*args)
}