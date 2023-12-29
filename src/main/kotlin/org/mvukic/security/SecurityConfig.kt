package org.mvukic.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter

// https://medium.com/@jaidenashmore/jwt-authentication-in-spring-boot-webflux-6880c96247c7
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration {

    @Bean
    fun webFluxSecurityFilterChain(
        http: ServerHttpSecurity,
        authenticationManager: MyAuthenticationManager,
        authenticationConverter: MyServerAuthenticationConverter
    ): SecurityWebFilterChain {
        val authenticationWebFilter = AuthenticationWebFilter(authenticationManager).apply {
            setServerAuthenticationConverter(authenticationConverter)
        }
        return http
            .authorizeExchange {
                it
                    .pathMatchers("/free").permitAll()
                    .anyExchange().authenticated()
            }
            .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .logout { it.disable() }
            .build()
    }
}