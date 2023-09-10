package org.mvukic

import io.klogging.Klogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter


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
    runApplication<SpringBootApp>(*args)
}