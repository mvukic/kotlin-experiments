package org.mvukic.routing

import org.mvukic.logging.withLoggingCtx
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RouterClass(private val handler: RouterHandler) {

    @Bean
    fun routerFn() = coRouter {
        context(::withLoggingCtx)

        GET("get1/{id}", handler::get1)
        GET("get2/{id}", handler::get2)
        GET("free", handler::free)
        GET("error", handler::error)
    }
}
