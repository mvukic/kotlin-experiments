package org.mvukic

import io.klogging.context.withLogContext
import org.springframework.core.Ordered
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

object WebFilterOrders {
    const val ERROR = Ordered.HIGHEST_PRECEDENCE
    const val START = Ordered.HIGHEST_PRECEDENCE + 1
    const val END = Ordered.LOWEST_PRECEDENCE
}

data class RequestAttributes(val id: String, val path: String, val method: String, val timestamp: Long, var user: String?) {

    fun getLogContext() = arrayOf(
        "requestId" to id, "path" to path, "method" to method
    )

    companion object {
        const val KEY = "RequestAttributes"
    }
}

suspend inline fun runWithLoggingContext(
    request: ServerRequest,
    crossinline handler: suspend (ServerRequest) -> ServerResponse
): ServerResponse {
    val requestAttributes = request.exchange().attributes[RequestAttributes.KEY] as RequestAttributes
    return withLogContext("requestId" to requestAttributes.id) { handler(request) }
}