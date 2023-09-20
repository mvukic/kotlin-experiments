package org.mvukic

import io.klogging.Klogging
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
class RouterHandler : Klogging {
    private val webClient = WebClient.builder().baseUrl("https://jsonplaceholder.typicode.com/todos/1").build()

    suspend fun get(request: ServerRequest): ServerResponse {
        logger.info("get before call")
        val response = webClient.get().retrieve().awaitBody<String>()
        logger.info("get after call")
        return ServerResponse.ok().bodyValueAndAwait(response)
    }

    suspend fun error(request: ServerRequest): ServerResponse {
        error("some error")
    }
}

@Configuration
class RouterClass(private val handler: RouterHandler) : Klogging {

    @Bean
    fun routerFn() = coRouter {
        GET("get") { runWithLoggingContext(it, handler::get) }
        GET("error") { runWithLoggingContext(it, handler::error) }
    }
}