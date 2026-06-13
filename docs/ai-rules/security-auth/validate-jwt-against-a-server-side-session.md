# Validate every JWT against a server-side session

A signed, unexpired JWT is not enough on its own: `JwtReactiveAuthenticationManager` also requires a matching row in `UserSessionRepositoryReactive` (`findByToken`) and an existing user before authenticating. When the session is missing, raise `MissingUserSessionException` so `JwtSecurityContextRepository` clears the cookie and returns 401. This is what makes logout (deleting the session) effective for revocation — never authenticate on token signature alone.

## Good

```kotlin
private fun authenticateWithValidToken(token: String, claims: Claims): Mono<Authentication> {
    return sessionsRepository
        .findByToken(token)
        .switchIfEmpty(Mono.error(MissingUserSessionException("Invalid token - no corresponding session found")))
        .flatMap { session ->
            userRepository
                .findByEmail(claims.extractSubject())
                .switchIfEmpty(Mono.error(MissingUserSessionException("Invalid token - no corresponding user found")))
                .map { user -> authenticatedToken(user!!, null) }
        }
}
```

## Bad

```kotlin
override fun authenticate(authentication: Authentication?): Mono<Authentication> {
    val token = authentication?.credentials as? String ?: return Mono.empty()
    // Trusts the signature only: revoked/logged-out tokens still authenticate
    val claims = jwtService.parseAndValidate(token).body
    return Mono.just(UsernamePasswordAuthenticationToken(claims.subject, null, listOf(SimpleGrantedAuthority("ROLE_USER"))))
}
```
