package org.mvukic

import io.klogging.context.withLogContext
import org.springframework.core.Ordered
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.ServerWebExchange
import java.time.Clock
import java.util.*
import kotlin.coroutines.CoroutineContext

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

    fun getLogContext() = arrayOf("requestId" to id, "path" to path, "method" to method, "user" to user)

    companion object {
        const val KEY = "RequestAttributes"

        fun fromExchange(exchange: ServerWebExchange) = RequestAttributes(
            id = UUID.randomUUID().toString(),
            path = exchange.request.path.toString(),
            method = exchange.request.method.toString(),
            timestamp = Clock.systemUTC().instant().toEpochMilli(),
            user = null
        )

    }
}

suspend inline fun runWithLoggingContext(
    request: ServerRequest,
    crossinline handler: suspend (ServerRequest) -> ServerResponse
): ServerResponse {
    /* Coroutine context */
    val coroutineContext = request.exchange().attributes[CoWebFilter.COROUTINE_CONTEXT_ATTRIBUTE] as CoroutineContext
    val requestAttributesCoroutineContext = coroutineContext[RequestAttributesCoroutineContext]!!

    /* Exchange attribute */
    val requestAttributes = request.exchange().attributes[RequestAttributes.KEY] as RequestAttributes

    return withLogContext(
        "requestIdAttr" to requestAttributes.id,
        "requestIdCtx" to requestAttributesCoroutineContext.requestAttributes.id
    ) { handler(request) }
}
