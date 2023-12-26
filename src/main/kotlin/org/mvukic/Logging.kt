package org.mvukic

import io.klogging.context.logContext
import org.springframework.core.Ordered
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.server.ServerWebExchange
import java.time.Clock
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

object WebFilterOrders {
    const val ERROR = Ordered.HIGHEST_PRECEDENCE
    const val START = Ordered.HIGHEST_PRECEDENCE + 1
    const val END = Ordered.LOWEST_PRECEDENCE
}

class RequestAttributesCoroutineContext(val requestAttributes: RequestAttributes) : CoroutineContext.Element {
    override val key: CoroutineContext.Key<RequestAttributesCoroutineContext> = Key

    companion object Key : CoroutineContext.Key<RequestAttributesCoroutineContext>
}

data class RequestAttributes(
    val id: String,
    val path: String,
    val method: String,
    val timestamp: Long,
    var user: String?
) {

    fun getEndRequestLogContext() = arrayOf("id" to id, "user" to user)
    fun getSimpleLogContext() = arrayOf("id" to id)

    companion object {
        fun fromExchange(exchange: ServerWebExchange) = RequestAttributes(
            id = UUID.randomUUID().toString(),
            path = exchange.request.path.toString(),
            method = exchange.request.method.toString(),
            timestamp = Clock.systemUTC().instant().toEpochMilli(),
            user = null
        )

    }
}

suspend fun withLoggingCtx(unused: ServerRequest): CoroutineContext {
    /* Get request attribute coroutine context */
    val requestAttributesCoroutineContext = coroutineContext[RequestAttributesCoroutineContext]!!
    /* Get request attribute */
    val requestAttributes = requestAttributesCoroutineContext.requestAttributes

    return logContext(*requestAttributes.getSimpleLogContext())
}