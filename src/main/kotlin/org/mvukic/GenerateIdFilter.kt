package org.mvukic

import io.klogging.Klogging
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange
import java.util.*

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RequestStartFilter : CoWebFilter(), Klogging {
    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        val request = exchange.request

        val requestId = UUID.randomUUID().toString()
        val method = request.method.toString()
        val path = request.path.value()

        logger.info("RequestStartFilter {requestId}, {method}, {path}", requestId, method, path)

        val requestCopy = request.mutate().header("requestId", requestId).build()
        val exchangeCopy = exchange.mutate().request(requestCopy).build()
        return chain.filter(exchangeCopy)
    }
}

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
class RequestEndFilter : CoWebFilter(), Klogging {
    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        val request = exchange.request
        val requestId = request.headers["requestId"]

        logger.info("RequestEndFilter {requestId}", requestId)

        return chain.filter(exchange)
    }
}