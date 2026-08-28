package com.ord.core.security

import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationResult
import org.springframework.security.authorization.ReactiveAuthorizationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authorization.AuthorizationContext
import reactor.core.publisher.Mono

class AnonymousOnlyAuthorizationManager : ReactiveAuthorizationManager<AuthorizationContext> {
    override fun authorize(
        authentication: Mono<Authentication>,
        context: AuthorizationContext
    ): Mono<AuthorizationResult> {
        return authentication
            .map<AuthorizationResult> { auth -> AuthorizationDecision(!auth.isAuthenticated) }
            .defaultIfEmpty(AuthorizationDecision(true))
    }
}
