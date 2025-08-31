package com.ord.core.auth.annotations

import com.ord.core.security.UserRepositoryReactive
import com.ord.core.user.model.UserEntity
import org.springframework.core.MethodParameter
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthenticatedUserArgumentResolver(
    private val userRepositoryReactive: UserRepositoryReactive
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(AuthenticatedUser::class.java) &&
                UserEntity::class.java.isAssignableFrom(parameter.parameterType)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        return exchange.getPrincipal<Authentication>() // Mono<Authentication>
            .flatMap { auth ->
                val email = auth.name // or (auth.principal as? CustomUserDetails)?.username
                userRepositoryReactive.findByEmail(email)
                    .switchIfEmpty(Mono.error(IllegalArgumentException("User not found")))
            }
            .cast(Any::class.java)
    }
}

