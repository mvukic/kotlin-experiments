package org.mvukic.filter

import io.klogging.Klogging
import io.klogging.context.withLogContext
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.mvukic.logging.RequestAttributesCoroutineContext
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange
import kotlin.coroutines.CoroutineContext


class AfterAuthenticationFilter : CoWebFilter(), Klogging {

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        /* Get coroutine context */
        val coroutineContext = exchange.attributes[COROUTINE_CONTEXT_ATTRIBUTE] as CoroutineContext
        /* Get request attribute coroutine context */
        val requestAttributesCoroutineContext = coroutineContext[RequestAttributesCoroutineContext]!!
        /* Get request attribute */
        val requestAttributes = requestAttributesCoroutineContext.requestAttributes

        // Get user and add it to request attributes object
        val securityContext = ReactiveSecurityContextHolder.getContext().awaitSingleOrNull()
        val authentication = securityContext?.authentication
        requestAttributes.user = authentication?.credentials.toString()

        withContext(requestAttributesCoroutineContext) {
            withLogContext(*requestAttributes.getIdAndUserLogContext()) {
                chain.filter(exchange)
            }
        }
    }
}