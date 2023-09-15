package org.mvukic

import io.klogging.context.Context
import io.klogging.java.LoggerFactory
import kotlinx.coroutines.reactor.ReactorContext
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter


@Service
class RouterHandler {

    private val logger = LoggerFactory.getLogger(RouterHandler::class.java)
    private val webClient = WebClient.builder().baseUrl("https://jsonplaceholder.typicode.com/todos/1").build()

    suspend fun get(request: ServerRequest): ServerResponse {
        logger.info("get")
        val response = webClient.get().retrieve().awaitBody<String>()
        return ServerResponse.ok().bodyValueAndAwait(response)
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


//https://github.com/spring-projects/spring-framework/issues/27522

fun main(args: Array<String>) {
    // Always logged
    Context.addBaseContext(
        "app" to "LoggingDemo",
        "version" to "1.2.0"
    )

    // Always logged but dynamically fetched from the ReactorContext
    Context.addContextItemExtractor(ReactorContext) {
        mapOf()
    }

    Context.addContextItemExtractor(RequestIdCoroutineContext) {
        mapOf()
    }
    Context.addContextItemExtractor(UserCoroutineContext) {
        mapOf()
    }

    Context.addItemExtractor {
        mapOf("test" to "value")
    }

    runApplication<SpringBootApp>(*args)
}