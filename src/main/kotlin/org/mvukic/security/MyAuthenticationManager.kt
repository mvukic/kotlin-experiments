package org.mvukic.security

import io.klogging.NoCoLogging
import org.mvukic.model.MyAppUser
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class MyAuthenticationManager : ReactiveAuthenticationManager, NoCoLogging {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val user = getUserDetailsOrError(authentication.credentials as String)
        return UsernamePasswordAuthenticationToken(
            user.name,
            user.name,
            user.roles.map { SimpleGrantedAuthority(it) }
        ).toMono()

    }
}

fun getUserDetailsOrError(value: String): MyAppUser {
    if (!isValid(value)) {
        error("Token format is not valid")
    }
    val (name, roles) = decodeToken(value)
    if (name != "user_name") {
        error("Token is not valid")
    }
    return MyAppUser(
        name = name,
        roles = roles.split(",").map { it.uppercase() }
    )
}

fun isValid(value: String): Boolean {
    val parts = value.trim().split(":")
    if (parts.size != 2) {
        return false
    }
    if (parts[0].isBlank()) {
        return false
    }

    if (parts[1].isBlank()) {
        return false
    }

    return true
}

fun decodeToken(value: String): List<String> {
    return value.trim().split(":")
}