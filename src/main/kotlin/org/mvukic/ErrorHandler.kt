package org.mvukic

import io.klogging.NoCoLogging
import io.klogging.context.withLogContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono


@Serializable
data class ErrorResponse(
    @SerialName("description")
    val description: String
)

@Configuration
@Order(WebFilterOrders.ERROR)
class ErrorHandler : ErrorWebExceptionHandler, NoCoLogging {

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {

        val requestAttributes = exchange.attributes[RequestAttributes.KEY] as RequestAttributes

        val error = Json.encodeToString(ErrorResponse("error")).encodeToByteArray()
        exchange.response.headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR

        val dataBuffer = exchange.response.bufferFactory()

//        withLogContext("requestId" to requestAttributes.id) {
            logger.error(ex, "ERROR")
//        }
        exchange.response.writeWith(dataBuffer.wrap(error).toMono())
        return Mono.empty()
    }

}