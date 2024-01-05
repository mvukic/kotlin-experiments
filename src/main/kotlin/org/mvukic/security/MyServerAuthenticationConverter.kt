package org.mvukic.security

import io.klogging.NoCoLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class MyServerAuthenticationConverter : ServerAuthenticationConverter, NoCoLogging {
    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        return Mono.justOrEmpty(exchange)
            .flatMap { Mono.justOrEmpty(exchange.request.headers.getOrEmpty("x-auth").firstOrNull() ?: "") }
            .filter { it.isNotEmpty() }
            .map { it }
            .map { UsernamePasswordAuthenticationToken(it, it) }
    }
}