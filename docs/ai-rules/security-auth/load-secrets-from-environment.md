# Load secrets from the environment, never hardcode them

Secrets (SMTP credentials, OpenAI key, DB password) must come from environment variables surfaced through `@ConfigurationProperties` classes like `OtpProperties` or `docker-compose.yaml` env wiring. Never inline a secret literal in Kotlin source, and never commit real values — `.env.example` holds only placeholders.

## Good

```kotlin
@Configuration
@ConfigurationProperties(prefix = "otp")
class OtpProperties(
    var codeForWhitelisted: String = "", // bound from OTP_CODE_FOR_WHITELISTED_EMAILS
)

@Service
class EmailServiceImpl(
    private val mailSender: JavaMailSender,
    @Value("\${email.from}") private val from: String,
)
```

## Bad

```kotlin
@Service
class EmailServiceImpl {
    // Hardcoded SMTP password committed to the repo
    private val password = "super-secret-prod-password"
}
```
