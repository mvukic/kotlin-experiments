package org.mvukic

import io.klogging.Klogging
import io.klogging.context.Context
import io.klogging.context.withLogContext
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
class RouterHandler : Klogging {
    private val webClient = WebClient.builder().baseUrl("https://jsonplaceholder.typicode.com/todos/1").build()

    suspend fun get(request: ServerRequest): ServerResponse {
        error("test")
        logger.info("get before call")
        val response = webClient.get().retrieve().awaitBody<String>()
        logger.info("get after call")
        return ServerResponse.ok().bodyValueAndAwait(response)
    }
}

@Configuration
class RouterClass(private val handler: RouterHandler) : Klogging {

    @Bean
    fun routerFn() = coRouter {
        filter { request, fn ->
            logger.info("RouterClass")
            fn(request)
        }

        GET("get") {
//            val requestCtx = it.exchange().getContextElement(RequestCoroutineContext)!!
//            val userCtx = it.exchange().getContextElement(UserCoroutineContext)!!
//            withLogContext(*(requestCtx.getLogContext() + userCtx.getLogContext())) {
                handler.get(it)
//            }

        }
    }
}

@SpringBootApplication
class SpringBootApp


//https://github.com/spring-projects/spring-framework/issues/27522

fun main(args: Array<String>) {
    Context.addBaseContext(
        "app" to "LoggingDemo",
        "version" to "1.2.0"
    )
    runApplication<SpringBootApp>(*args)
}