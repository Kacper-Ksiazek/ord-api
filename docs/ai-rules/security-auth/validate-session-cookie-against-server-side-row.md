# Validate the session cookie against a server-side row

The `AUTH-TOKEN` cookie is an opaque secret, not a JWT. `SessionReactiveAuthenticationManager` hashes it (`SHA-256`) and requires a matching, unexpired row in `UserSessionRepositoryReactive` (`findByTokenHash`) plus an existing user before authenticating. When the session is missing or past `idleExpiresAt` / `absoluteExpiresAt`, raise `MissingUserSessionException` so `SessionSecurityContextRepository` clears the cookie and returns 401. Logout deletes the row — never authenticate from a cookie that has no database session.

## Good

```kotlin
return sessionsRepository
    .findByTokenHash(tokenHash)
    .switchIfEmpty(Mono.error(MissingUserSessionException("Invalid token - no corresponding session found")))
    .flatMap { session ->
        if (session.isExpired()) {
            Mono.error(MissingUserSessionException("Session expired"))
        } else {
            userRepository.findById(session.userId)
        }
    }
```

## Bad

```kotlin
override fun authenticate(authentication: Authentication?): Mono<Authentication> {
    val token = authentication?.credentials as? String ?: return Mono.empty()
    // Trusts a signed JWT only: revoked/logged-out tokens still authenticate
    val claims = jwtService.parseAndValidate(token).body
    return Mono.just(UsernamePasswordAuthenticationToken(claims.subject, null, listOf(SimpleGrantedAuthority("ROLE_USER"))))
}
```
