package org.mvukic.error

import io.klogging.Klogging
import io.klogging.context.withLogContext
import kotlinx.coroutines.reactor.mono
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.mvukic.logging.RequestAttributesCoroutineContext
import org.mvukic.logging.WebFilterOrders
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import kotlin.coroutines.CoroutineContext


@Serializable
data class ErrorResponse(
    @SerialName("description")
    val description: String,

    @SerialName("requestId")
    val requestId: String

)

@Configuration
@Order(WebFilterOrders.ERROR)
class ErrorHandler : ErrorWebExceptionHandler, Klogging {

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        /* Get coroutine context */
        val coroutineContext = exchange.attributes[CoWebFilter.COROUTINE_CONTEXT_ATTRIBUTE] as CoroutineContext
        /* Get request attribute coroutine context */
        val requestAttributesCoroutineContext = coroutineContext[RequestAttributesCoroutineContext]!!
        /* Get request attribute */
        val requestAttributes = requestAttributesCoroutineContext.requestAttributes

        /* Create dummy Mono object */
        val log = mono {
            withLogContext(*requestAttributes.getIdLogContext()) {
                logger.error(ex, "ERROR")
            }
        }

        val error = Json.encodeToString(ErrorResponse("error", requestAttributes.id)).encodeToByteArray()
        exchange.response.headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR


        val dataBuffer = exchange.response.bufferFactory()
        return log.then(exchange.response.writeWith(dataBuffer.wrap(error).toMono()))
    }

}
