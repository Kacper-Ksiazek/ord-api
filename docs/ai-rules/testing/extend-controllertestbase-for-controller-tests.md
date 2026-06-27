# Extend ControllerTestBase for controller tests

Controller tests must extend `ControllerTestBase` and forward the shared dependencies (`webClient`, `jwtProperties`, repositories, `passwordEncoder`, `gptTokensUsageRepository`) through the constructor. `ControllerTestBase` extends `TestcontainersConfig`, which wires the Postgres Testcontainer, runs Flyway migrations, and configures R2DBC — so by extending it you get a real database plus `faker`, `mockAuthenticatedUser(...)`, and `assertGptTokensLogCreated(...)` for free. Inject feature-specific repositories as extra `@Autowired` constructor params.

## Good

```kotlin
class TestConversationController @Autowired constructor(
    private val conversationRepository: ConversationRepository,
    webClient: WebTestClient,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    userRepository: UserRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder,
    gptTokensUsageRepository: GptTokensUsageRepository
) : ControllerTestBase(
    webClient = webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder,
    gptTokensUsageRepository = gptTokensUsageRepository
)
```

## Bad

```kotlin
// Re-implements auth, faker, and container setup instead of reusing the base.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestConversationController {
    @Autowired lateinit var webClient: WebTestClient
    // no Testcontainers/Flyway wiring, no mockAuthenticatedUser,
    // no assertGptTokensLogCreated -> auth and DB plumbing copy-pasted everywhere
}
```
