# Declare JWT security with @SecurityRequirement(name = "bearer-jwt")

Authenticated controllers annotate the class with `@SecurityRequirement(name = "bearer-jwt")`; controllers that mix public and protected endpoints (like auth) put the annotation on the protected methods only. The `"bearer-jwt"` scheme name must match the security scheme registered in `OpenApiConfig`, so Swagger UI's Authorize button wires up correctly.

## Good

```kotlin
@RestController
@RequestMapping("/api/v1/conversations")
@SecurityRequirement(name = "bearer-jwt")   // all endpoints require JWT
class ConversationController(...)
```

```kotlin
// AuthController — public endpoints stay open, logout is protected
@DeleteMapping("/logout")
@SecurityRequirement(name = "bearer-jwt")
fun logout(...): Mono<ResponseEntity<Void>> = authFacade.logout(exchange)
```

## Bad

```kotlin
@RestController
@RequestMapping("/api/v1/conversations")
@SecurityRequirement(name = "jwt")   // name does not match the scheme in OpenApiConfig
class ConversationController(...)     // Authorize button won't apply the token
```
