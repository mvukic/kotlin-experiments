package org.mvukic

import io.klogging.java.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange
import java.util.*


//@Configuration
//@Order(Ordered.HIGHEST_PRECEDENCE)
//class ErrorHandler : ErrorWebExceptionHandler {
//    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
//        return Mono.empty()
//    }
//}

data class User(val name: String)


// Generate id
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RequestStartFilter : CoWebFilter() {

    private val logger = LoggerFactory.getLogger(RequestStartFilter::class.java)

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        val request = exchange.request

        val requestId = UUID.randomUUID().toString()

        // Save id as an exchange attribute
        exchange.attributes["requestId"] = requestId

        val method = request.method.toString()
        val path = request.path.value()
        logger.info("RequestStartFilter {requestId}, {method}, {path}", requestId, method, path)

        return chain.filter(exchange)
    }
}

// Get and save user data
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
class AuthenticationFilter : CoWebFilter() {

    private val logger = LoggerFactory.getLogger(AuthenticationFilter::class.java)

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        val user = /* get user from request */ User("user")

        // Save user as an exchange attribute
        exchange.attributes["user"] = user

        logger.info("AuthenticationFilter {user}", user.name)

        return chain.filter(exchange)
    }
}


// Log end request
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
class RequestEndFilter : CoWebFilter() {

    private val logger = LoggerFactory.getLogger(RequestEndFilter::class.java)

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        val requestId = exchange.getAttribute<String>("requestId")!!
        logger.info("RequestEndFilter {requestId} {user}", requestId)
        return chain.filter(exchange)
    }
}