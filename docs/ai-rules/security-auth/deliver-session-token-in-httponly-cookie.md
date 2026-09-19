# Deliver the session token in an HttpOnly cookie

The session token is never returned in a response body or read from an `Authorization` header. It is an opaque CSPRNG value written to the configured `session.cookie-name` cookie with `httpOnly(true)`, and `SessionSecurityContextRepository` reads it back from that same cookie. Persist only `SHA-256(token)` in `user_sessions`. Keep issuing and clearing the token through the cookie helpers (`addAuthTokenCookie`, `invalidateAuthTokenCookie`) so the raw token stays inaccessible to client-side JavaScript. Do not rotate the cookie value on each request.

## Good

```kotlin
fun ServerWebExchange.addAuthTokenCookie(
    name: String,
    value: String
) {
    val cookie = ResponseCookie
        .from(name, value)
        .httpOnly(true)
        .path("/")
        .build()

    this.response.addCookie(cookie)
}
```

## Bad

```kotlin
override fun verifyOtp(email: String, code: String): Mono<ResponseEntity<Map<String, String>>> {
    val token = sessionTokenService.generateRawToken()
    // Returning the session token in the body exposes it to JavaScript / XSS exfiltration
    return Mono.just(ResponseEntity.ok(mapOf("token" to token)))
}
```
