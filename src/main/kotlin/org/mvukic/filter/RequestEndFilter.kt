package org.mvukic.filter

import io.klogging.Klogging
import io.klogging.context.withLogContext
import org.mvukic.logging.RequestAttributesCoroutineContext
import org.mvukic.logging.WebFilterOrders
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange
import kotlin.coroutines.CoroutineContext

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

        withLogContext(*requestAttributes.getRequestEndLogContext()) {
            logger.info("END")
            chain.filter(exchange)
        }
    }
}
