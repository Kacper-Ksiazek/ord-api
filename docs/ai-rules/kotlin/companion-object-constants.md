# Hoist magic values into `companion object` constants

Put shared literals (separators, route lists, configuration keys) into a `companion object` as `const val` or `private val` instead of repeating string/array literals inline. Name them in `UPPER_SNAKE_CASE`. This centralizes the value and documents its intent.

## Good

```kotlin
interface OpenAIAPIClientService {
    companion object {
        const val STREAMING_CONTENT_SEPARATOR: String = "[[BREAK]]"
    }
    // ...
}

@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration(/* ... */) {
    companion object {
        private val ANONYMOUS_PATHS = arrayOf(
            "/api/v1/auth/otp-request",
            "/api/v1/auth/otp-verify",
            "/api/v1/health-check"
        )
    }
}
```

## Bad

```kotlin
class SecurityConfiguration {
    fun apiSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            // Magic literals scattered inline, duplicated across methods, hard to keep in sync
            .authorizeExchange { ex ->
                ex.pathMatchers("/api/v1/auth/otp-request", "/api/v1/health-check").permitAll()
            }
            .build()
    }
}
```
