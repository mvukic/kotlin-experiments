package org.mvukic

import io.klogging.Klogging
import io.klogging.context.withLogContext
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*
import kotlin.coroutines.CoroutineContext

data class User(val name: String)

class UserCoroutineContext(val value: User) : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> = Key

    companion object Key : CoroutineContext.Key<UserCoroutineContext>

    fun getLogContext() = arrayOf(
        "user" to value.name
    )

}

class RequestCoroutineContext(val id: String, val path: String, val method: String, val timestamp: Instant) : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> = Key

    companion object Key : CoroutineContext.Key<RequestCoroutineContext>


    fun getLogContext() = arrayOf(
        "requestId" to id, "path" to path, "method" to method
    )

}

// Generate id
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RequestStartFilter : CoWebFilter(), Klogging {

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        val requestCtx = RequestCoroutineContext(
            id = UUID.randomUUID().toString(),
            path = exchange.request.path.toString(),
            method = exchange.request.method.toString(),
            timestamp = Clock.System.now()
        )

        withLogContext(*requestCtx.getLogContext()) {
            logger.info("RequestStartFilter")
        }

        withContext(requestCtx) {
            chain.filter(exchange)
        }
    }
}

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
class ErrorHandler : ErrorWebExceptionHandler {
    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        val requestCtx = exchange.getContextElement(RequestCoroutineContext)
        val userCtx = exchange.getContextElement(UserCoroutineContext)
        println(requestCtx)
        println(userCtx)
        return Mono.empty()
    }
}

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
class AuthenticationFilter : CoWebFilter(), Klogging {

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        val user = User("user name")

        val requestCtx = exchange.getContextElement(RequestCoroutineContext)!!
        val userCtx = UserCoroutineContext(user)

        withLogContext(*(requestCtx.getLogContext() + userCtx.getLogContext())) {
            logger.info("AuthenticationFilter")
        }

        withContext(requestCtx + userCtx) {
            chain.filter(exchange)
        }
    }
}

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
class RequestEndFilter : CoWebFilter(), Klogging {

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {

        val requestCtx = exchange.getContextElement(RequestCoroutineContext)!!
        val userCtx = exchange.getContextElement(UserCoroutineContext)!!

        val duration = (Clock.System.now() - requestCtx.timestamp).inWholeNanoseconds
        withLogContext(*(requestCtx.getLogContext() + userCtx.getLogContext() + arrayOf("duration" to duration.toString()))) {
            logger.info("RequestEndFilter")
        }
        chain.filter(exchange)
    }
}

fun ServerWebExchange.getContext(): CoroutineContext {
    return attributes[CoWebFilter.COROUTINE_CONTEXT_ATTRIBUTE] as CoroutineContext
}

fun <T : CoroutineContext.Element> ServerWebExchange.getContextElement(key: CoroutineContext.Key<T>): T? {
    return getContext()[key]
}