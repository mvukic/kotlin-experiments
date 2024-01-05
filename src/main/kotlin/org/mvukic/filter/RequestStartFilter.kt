package org.mvukic.filter

import io.klogging.Klogging
import io.klogging.context.withLogContext
import kotlinx.coroutines.withContext
import org.mvukic.logging.RequestAttributes
import org.mvukic.logging.RequestAttributesCoroutineContext
import org.mvukic.logging.WebFilterOrders
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange

@Component
@Order(WebFilterOrders.START)
class RequestStartFilter : CoWebFilter(), Klogging {

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        // Create request attributes
        val requestAttributes = RequestAttributes.fromExchange(exchange)
        // Create request attributes coroutine context
        val requestAttributesCoroutineContext = RequestAttributesCoroutineContext(requestAttributes)

        withLogContext(*requestAttributes.getLogContext()) {
            logger.info("START")
        }

        withContext(requestAttributesCoroutineContext) {
            chain.filter(exchange)
        }
    }
}