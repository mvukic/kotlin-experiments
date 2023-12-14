package org.mvukic.routing

import org.mvukic.runWithLoggingContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RouterClass(private val handler: RouterHandler) {

    @Bean
    fun routerFn() = coRouter {

        filter(::runWithLoggingContext)

        GET("get1/{id}", handler::get1)
        GET("get2/{id}", handler::get2)
        GET("error", handler::error)
    }
}
