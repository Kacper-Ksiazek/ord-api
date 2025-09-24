package com.ord.config.security

import com.ord.core.security.AnonymousOnlyAuthorizationManager
import com.ord.core.security.JwtReactiveAuthenticationManager
import com.ord.core.security.JwtRenewalWriteFilter
import com.ord.core.security.JwtSecurityContextRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration(
    private val authManager: JwtReactiveAuthenticationManager,
    private val contextRepository: JwtSecurityContextRepository,
    private val renewalFilter: JwtRenewalWriteFilter
) {
    val anonymousOnlyAuthorizationManager = AnonymousOnlyAuthorizationManager()

    companion object {
        private val ANONYMOUS_PATHS = arrayOf(
            "/api/v1/auth/login",
            "/api/v1/auth/register"
        )

        private val AUTHORIZED_PATHS = arrayOf(
            "/api/v1/auth/me",
            "/api/v1/auth/logout",
            "/api/v1/words/**",
            "/api/v1/games/**",
            "/api/v1/conversations/**",
        )
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .securityContextRepository(contextRepository)
            .authenticationManager(authManager)
            .authorizeExchange { ex ->
                ex.pathMatchers(*ANONYMOUS_PATHS).access(anonymousOnlyAuthorizationManager)
                ex.pathMatchers(*AUTHORIZED_PATHS).authenticated()
            }
            .addFilterAfter(
                renewalFilter,
                SecurityWebFiltersOrder.SECURITY_CONTEXT_SERVER_WEB_EXCHANGE
            )
            .build()
    }

}
