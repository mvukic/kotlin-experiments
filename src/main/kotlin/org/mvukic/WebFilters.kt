package org.mvukic

import io.klogging.Klogging
import io.klogging.context.withLogContext
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.Clock
import java.util.*


@Component
@Order(WebFilterOrders.START)
class RequestStartFilter : CoWebFilter(), Klogging {

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        val requestAttributes = RequestAttributes(
            id = UUID.randomUUID().toString(),
            path = exchange.request.path.toString(),
            method = exchange.request.method.toString(),
            timestamp = Clock.systemUTC().instant().toEpochMilli(),
            user = null
        )

        withLogContext(*requestAttributes.getLogContext()) {
            logger.info("RequestStartFilter")
        }

        exchange.attributes[RequestAttributes.KEY] = requestAttributes
        chain.filter(exchange)
    }
}

@Component
@Order(WebFilterOrders.END)
class RequestEndFilter : CoWebFilter(), Klogging {

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {

        val requestAttributes = exchange.attributes[RequestAttributes.KEY] as RequestAttributes

        val duration = Clock.systemUTC().instant().toEpochMilli() - requestAttributes.timestamp
        val logContext = requestAttributes.getLogContext() + arrayOf("duration" to duration.toString())

        withLogContext(*logContext) {
            logger.info("RequestEndFilter")
        }
        chain.filter(exchange)
    }
}