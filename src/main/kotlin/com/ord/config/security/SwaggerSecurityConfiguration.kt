package com.ord.config.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher

@Configuration
class SwaggerSecurityConfiguration(
    private val passwordEncoder: PasswordEncoder
) {
    @Value("\${swagger.basic-auth.username:admin}")
    private lateinit var swaggerUsername: String

    @Value("\${swagger.basic-auth.password:admin}")
    private lateinit var swaggerPassword: String

    companion object {
        val SWAGGER_PATHS = arrayOf(
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v3/api-docs",
            "/webjars/**"
        )
    }

    @Bean
    fun swaggerUserDetailsService(): MapReactiveUserDetailsService {
        val user = User.builder()
            .username(swaggerUsername)
            .password(passwordEncoder.encode(swaggerPassword))
            .roles("SWAGGER")
            .build()
        return MapReactiveUserDetailsService(user)
    }

    @Bean
    @Order(1)
    fun swaggerSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        val matchers = SWAGGER_PATHS.map { PathPatternParserServerWebExchangeMatcher(it) }
        val combinedMatcher = OrServerWebExchangeMatcher(matchers)

        return http
            .securityMatcher(combinedMatcher)
            .authorizeExchange { ex ->
                ex.anyExchange().authenticated()
            }
            .httpBasic { basic ->
                basic.authenticationManager(
                    UserDetailsRepositoryReactiveAuthenticationManager(
                        swaggerUserDetailsService()
                    ).apply {
                        setPasswordEncoder(passwordEncoder)
                    }
                )
            }
            .csrf { it.disable() }
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .build()
    }
}
