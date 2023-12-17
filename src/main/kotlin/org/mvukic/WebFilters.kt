package org.mvukic

import io.klogging.Klogging
import io.klogging.context.withLogContext
import kotlinx.coroutines.withContext
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange
import kotlin.coroutines.CoroutineContext


@Component
@Order(WebFilterOrders.START)
class RequestStartFilter : CoWebFilter(), Klogging {

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        // Create request attributes
        val requestAttributes = RequestAttributes.fromExchange(exchange)
        // Create request attributes coroutine context
        val requestAttributesCoroutineContext = RequestAttributesCoroutineContext(requestAttributes)

        withContext(requestAttributesCoroutineContext) {
            withLogContext(*requestAttributes.getStartRequestLogContext()) {
                logger.info("START")
                chain.filter(exchange)
            }
        }
    }
}

@Component
@Order(WebFilterOrders.END)
class RequestEndFilter : CoWebFilter(), Klogging {

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        /* Get coroutine context */
        val coroutineContext = exchange.attributes[COROUTINE_CONTEXT_ATTRIBUTE] as CoroutineContext
        /* Get request attribute coroutine context */
        val requestAttributesCoroutineContext = coroutineContext[RequestAttributesCoroutineContext]!!
        /* Get request attribute */
        val requestAttributes = requestAttributesCoroutineContext.requestAttributes

        withLogContext(*requestAttributes.getEndRequestLogContext()) {
            logger.info("END")
            chain.filter(exchange)
        }
    }
}
