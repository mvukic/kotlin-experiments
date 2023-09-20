package org.mvukic

import io.klogging.context.withLogContext
import kotlinx.serialization.Serializable
import org.springframework.core.Ordered
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

object WebFilterOrders {
    const val START = Ordered.HIGHEST_PRECEDENCE + 1
    const val ERROR = Ordered.HIGHEST_PRECEDENCE + 2
    const val END = Ordered.LOWEST_PRECEDENCE
}

@Serializable
data class ErrorResponse(val description: String)

data class RequestAttributes(val id: String, val path: String, val method: String, val timestamp: Long) {

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
    return withLogContext(*requestAttributes.getLogContext()) { handler(request) }
}