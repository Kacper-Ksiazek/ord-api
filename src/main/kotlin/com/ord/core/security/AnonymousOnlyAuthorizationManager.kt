package com.ord.core.security

import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.ReactiveAuthorizationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authorization.AuthorizationContext
import reactor.core.publisher.Mono

class AnonymousOnlyAuthorizationManager : ReactiveAuthorizationManager<AuthorizationContext> {
    override fun check(
        authentication: Mono<Authentication>,
        context: AuthorizationContext
    ): Mono<AuthorizationDecision> {
        return authentication
            .map { auth -> AuthorizationDecision(!auth.isAuthenticated) }
            .defaultIfEmpty(AuthorizationDecision(true)) // no auth means anonymous
    }
}

