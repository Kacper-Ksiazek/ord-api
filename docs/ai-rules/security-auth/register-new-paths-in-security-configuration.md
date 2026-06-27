# Register every new endpoint path in SecurityConfiguration

The reactive filter chain only authorizes paths it knows about. When you add a controller, register its base path in `AUTHORIZED_PATHS` (requires a valid session) or, for truly public endpoints, in `ANONYMOUS_PATHS` (guarded by `AnonymousOnlyAuthorizationManager`). A path listed in neither array is not matched by any rule, so decide ownership explicitly rather than leaving it unconfigured.

## Good

```kotlin
private val AUTHORIZED_PATHS = arrayOf(
    "/api/v1/auth/logout",
    "/api/v1/users/**",
    "/api/v1/words/**",
    "/api/v1/games/**",
    "/api/v1/conversations/**",
    "/api/v1/language-proficiencies/**",
    "/api/v1/ai-explainer/**",
    "/api/v1/flashcards/**", // newly added feature path
)
```

## Bad

```kotlin
@RestController
@RequestMapping("/api/v1/flashcards") // never added to AUTHORIZED_PATHS / ANONYMOUS_PATHS
class FlashcardsController(/* ... */) {
    // Authorization for this path is undefined in SecurityConfiguration
}
```
