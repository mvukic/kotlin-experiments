package org.mvukic

import io.klogging.context.withLogContext
import io.klogging.java.LoggerFactory
import kotlinx.coroutines.withContext
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext


//@Configuration
//@Order(Ordered.HIGHEST_PRECEDENCE)
//class ErrorHandler : ErrorWebExceptionHandler {
//    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
//        return Mono.empty()
//    }
//}

data class User(val name: String)

class RequestIdCoroutineContext(val value: String) : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> = Key
    companion object Key : CoroutineContext.Key<RequestIdCoroutineContext>
    override fun toString() = value
}

class UserCoroutineContext(val value: User) : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> = Key
    companion object Key : CoroutineContext.Key<UserCoroutineContext>
    override fun toString() = value.name
}

// Generate id
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RequestStartFilter : CoWebFilter() {

    private val logger = LoggerFactory.getLogger(RequestStartFilter::class.java)

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        val requestId = UUID.randomUUID().toString()
        logger.info("RequestStartFilter '{requestId}'", requestId)

        // Save id into coroutine context
        return withContext(coroutineContext + RequestIdCoroutineContext(requestId)) {
            chain.filter(exchange)
        }
    }
}

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
class AuthenticationFilter : CoWebFilter() {

    private val logger = LoggerFactory.getLogger(AuthenticationFilter::class.java)

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        val user = User("user name")

        val context = exchange.attributes[COROUTINE_CONTEXT_ATTRIBUTE] as CoroutineContext
        val requestId = context[RequestIdCoroutineContext]!!

        logger.info("AuthenticationFilter '{requestId}' '{user}'", requestId, user.name)

        // Save user into coroutine context
        return withContext(coroutineContext + context + UserCoroutineContext(user)) {
            chain.filter(exchange)
        }
    }
}

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
class RequestEndFilter : CoWebFilter() {

    private val logger = LoggerFactory.getLogger(RequestEndFilter::class.java)

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        val context = exchange.attributes[COROUTINE_CONTEXT_ATTRIBUTE] as CoroutineContext

        val requestId = context[RequestIdCoroutineContext]!!
        val user = context[UserCoroutineContext]!!

        logger.info("RequestEndFilter '{requestId}' '{user}'", requestId, user)
        withContext(coroutineContext + context) {
            chain.filter(exchange)
        }
    }
}