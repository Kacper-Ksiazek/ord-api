# Declare JWT security with @SecurityRequirement(name = OpenApiSecurity.AUTH_COOKIE)

Authenticated controllers annotate the class with `@SecurityRequirement(name = OpenApiSecurity.AUTH_COOKIE)`; controllers that mix public and protected endpoints (like auth) put the annotation on the protected methods only. The scheme name must match the cookie security scheme registered in `OpenApiConfig` (`auth-cookie`, `AUTH-TOKEN` cookie).

Runtime auth reads JWT from the `AUTH-TOKEN` HttpOnly cookie only — not from `Authorization: Bearer`.

## Good

```kotlin
@RestController
@RequestMapping("/api/v1/conversations")
@SecurityRequirement(name = OpenApiSecurity.AUTH_COOKIE)   // all endpoints require JWT cookie
class ConversationController(...)
```

```kotlin
// AuthController — public endpoints stay open, logout is protected
@DeleteMapping("/logout")
@SecurityRequirement(name = OpenApiSecurity.AUTH_COOKIE)
fun logout(...): Mono<ResponseEntity<Void>> = authFacade.logout(exchange)
```

## Bad

```kotlin
@RestController
@RequestMapping("/api/v1/conversations")
@SecurityRequirement(name = "bearer-jwt")   // wrong scheme; runtime does not read Bearer header
class ConversationController(...)
```

```kotlin
@SecurityRequirement(name = "jwt")   // name does not match the scheme in OpenApiConfig
class ConversationController(...)     // Authorize button won't apply the cookie
```
