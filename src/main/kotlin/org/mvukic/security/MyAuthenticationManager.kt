package org.mvukic.security

import io.klogging.NoCoLogging
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MyAuthenticationManager : ReactiveAuthenticationManager, NoCoLogging {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.just(authentication)
            .map { mapOrError(it.credentials as String) }
            .onErrorResume { Mono.empty() }
            .map { token ->
                UsernamePasswordAuthenticationToken(
                    "user",
                    token,
                    mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
                )
            }
    }
}

fun mapOrError(value: String): String {
    return if (value == "tkn") value else error("Invalid token")
}