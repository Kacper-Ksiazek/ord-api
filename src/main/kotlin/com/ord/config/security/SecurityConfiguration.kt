package com.ord.config.security

import com.ord.core.security.AnonymousOnlyAuthorizationManager
import com.ord.core.security.ApiAuthenticationEntryPoint
import com.ord.core.security.JwtReactiveAuthenticationManager
import com.ord.core.security.JwtSecurityContextRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration(
    private val authManager: JwtReactiveAuthenticationManager,
    private val contextRepository: JwtSecurityContextRepository,
    private val apiAuthenticationEntryPoint: ApiAuthenticationEntryPoint,
) {
    val anonymousOnlyAuthorizationManager = AnonymousOnlyAuthorizationManager()

    companion object {
        private val ANONYMOUS_PATHS = arrayOf(
            "/api/v1/auth/otp-request",
            "/api/v1/auth/otp-verify",
            "/api/v1/public/quickly-added-words/**",
            "/api/v1/health-check"
        )

        private val AUTHORIZED_PATHS = arrayOf(
            "/api/v1/auth/logout",
            "/api/v1/users/**",
            "/api/v1/words/**",
            "/api/v1/games/**",
            "/api/v1/conversations/**",
            "/api/v1/language-proficiencies/**",
            "/api/v1/quickly-added-words/**",
            "/api/v1/ai-explainer/**",
            "/api/v1/tts/**",
        )
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Order(2)
    fun apiSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .securityContextRepository(contextRepository)
            .authenticationManager(authManager)
            .authorizeExchange { ex ->
                ex.pathMatchers(*SwaggerSecurityConfiguration.SWAGGER_PATHS).denyAll()
                ex.pathMatchers(*ANONYMOUS_PATHS).access(anonymousOnlyAuthorizationManager)
                ex.pathMatchers(*AUTHORIZED_PATHS).authenticated()
            }
            .exceptionHandling { it.authenticationEntryPoint(apiAuthenticationEntryPoint) }
            .build()
    }
}
