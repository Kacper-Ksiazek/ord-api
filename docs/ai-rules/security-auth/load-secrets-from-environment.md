# Load secrets from the environment, never hardcode them

Secrets (JWT signing key, SMTP credentials, OpenAI key, DB password) must come from environment variables surfaced through `@ConfigurationProperties` classes like `JwtProperties` or `docker-compose.yaml` env wiring. Never inline a secret literal in Kotlin source, and never commit real values — `.env.example` holds only placeholders.

## Good

```kotlin
@Configuration
@ConfigurationProperties(prefix = "jwt")
class JwtProperties(
    var authCookieName: String = "",
    var secretKey: String = "", // bound from JWT_SECRET_KEY at runtime
    var expirationTime: Long = 900,
    val issuer: String = "ord-api"
)

@Service
class JwtService(private val jwtProperties: JwtProperties) {
    private val key = Keys.hmacShaKeyFor(
        jwtProperties.secretKey.toByteArray(StandardCharsets.UTF_8)
    )
}
```

## Bad

```kotlin
@Service
class JwtService {
    // Hardcoded signing key committed to the repo: anyone can forge tokens
    private val key = Keys.hmacShaKeyFor(
        "super-secret-prod-key-123".toByteArray(StandardCharsets.UTF_8)
    )
}
```
