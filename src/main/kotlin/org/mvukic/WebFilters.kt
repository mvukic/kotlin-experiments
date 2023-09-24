package org.mvukic

import io.klogging.Klogging
import io.klogging.context.withLogContext
import kotlinx.coroutines.withContext
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange
import java.time.Clock
import kotlin.coroutines.CoroutineContext


@Component
@Order(WebFilterOrders.START)
class RequestStartFilter : CoWebFilter(), Klogging {

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        val requestAttributes = RequestAttributes.fromExchange(exchange)

        /* Exchange attribute */
        exchange.attributes[RequestAttributes.KEY] = requestAttributes

        withLogContext(*requestAttributes.getLogContext()) {
            logger.info("RequestStartFilter")
        }

        /* Coroutine context */
        val requestAttributesCoroutineContext = RequestAttributesCoroutineContext(requestAttributes)
        withContext(requestAttributesCoroutineContext) {
            chain.filter(exchange)
        }
    }
}

@Component
@Order(WebFilterOrders.END)
class RequestEndFilter : CoWebFilter(), Klogging {

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        /* Exchange attribute */
        val requestAttributes = exchange.attributes[RequestAttributes.KEY] as RequestAttributes
        val duration = Clock.systemUTC().instant().toEpochMilli() - requestAttributes.timestamp

        withLogContext("requestId" to requestAttributes.id, "duration" to duration.toString()) {
            logger.info("RequestEndFilter")
        }

        /* Coroutine context */
        val coroutineContext = exchange.attributes[COROUTINE_CONTEXT_ATTRIBUTE] as CoroutineContext
        val requestAttributesCoroutineContext = coroutineContext[RequestAttributesCoroutineContext]!!

        withLogContext(
            "requestId" to requestAttributesCoroutineContext.requestAttributes.id,
            "duration" to duration.toString()
        ) {
            logger.info("RequestEndFilter")
        }

        withContext(requestAttributesCoroutineContext) {
            chain.filter(exchange)
        }
    }
}
