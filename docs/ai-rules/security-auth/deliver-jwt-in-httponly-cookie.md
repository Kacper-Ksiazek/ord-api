# Deliver the JWT in an HttpOnly cookie

The auth token is never returned in a response body or read from an `Authorization` header. It is written to the configured `authCookieName` cookie with `httpOnly(true)`, and `JwtSecurityContextRepository` reads it back from that same cookie. Keep issuing, renewing, and clearing the token through the cookie helpers (`addAuthTokenCookie`, `invalidateAuthTokenCookie`) so the token stays inaccessible to client-side JavaScript.

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
    val token = jwtService.createToken(subject = email)
    // Returning the JWT in the body exposes it to JavaScript / XSS exfiltration
    return Mono.just(ResponseEntity.ok(mapOf("token" to token)))
}
```
