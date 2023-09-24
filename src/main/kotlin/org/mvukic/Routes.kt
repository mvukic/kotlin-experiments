package org.mvukic

import io.klogging.Klogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Service
class RouterHandler : Klogging {

    suspend fun get1(request: ServerRequest): ServerResponse {
        val webClient = WebClient.builder().baseUrl("https://jsonplaceholder.typicode.com/todos/1").build()
        logger.info("get before call")
        val response = webClient.get().retrieve().awaitBody<String>()
        logger.info("get after call")
        return ServerResponse.ok().bodyValueAndAwait(response)
    }

    suspend fun get2(request: ServerRequest): ServerResponse {
        val restClient = RestClient.builder().baseUrl("https://jsonplaceholder.typicode.com/todos/1").build()
        logger.info("get before call")
        val response: String? = restClient.get().retrieve().body(String::class.java)
        logger.info("get after call")
        return ServerResponse.ok().bodyValueAndAwait(response ?: "")
    }

    suspend fun error(request: ServerRequest): ServerResponse {
        error("some error")
    }
}

@Configuration
class RouterClass(private val handler: RouterHandler) {

    @Bean
    fun routerFn() = coRouter {

        filter(::runWithLoggingContext)

        GET("get1", handler::get1)
        GET("get2", handler::get2)
        GET("error", handler::error)
    }
}
