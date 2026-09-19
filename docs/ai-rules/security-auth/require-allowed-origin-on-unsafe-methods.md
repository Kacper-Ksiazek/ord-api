# Require an allowed Origin on unsafe HTTP methods

Cookie auth is cross-origin (`SameSite=None` in production), so Spring CSRF stays disabled. `CorsWebFilter` already rejects credentialed JS from unknown origins, but a request with no `Origin` skips CORS entirely. `CsrfOriginWebFilter` therefore requires `POST` / `PUT` / `PATCH` / `DELETE` to send `Origin` matching `cors.allowed-origins` (never `*`). Browsers send this header on fetch/XHR/form posts to the API; tests set it via `WebTestClientOriginHeaderConfiguration`. Do not treat a missing Origin as same-site.

## Good

```kotlin
val origin = exchange.request.headers.origin
if (origin != null && origin in allowedOrigins) {
    return chain.filter(exchange)
}
return reject(exchange)
```

## Bad

```kotlin
// Unsafe methods proceed when Origin is absent — that is the CSRF hole CORS does not cover.
if (exchange.request.headers.origin == null) {
    return chain.filter(exchange)
}
```
