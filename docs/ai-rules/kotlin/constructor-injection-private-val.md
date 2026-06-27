# Inject dependencies via the primary constructor as `private val`

Wire Spring components (`@Service`, `@Component`, `@Configuration`) through their primary constructor, declaring each dependency as `private val`. Do not use field injection (`@Autowired` on properties) or `lateinit var`. This makes dependencies explicit, immutable, and easy to test.

## Good

```kotlin
@Service
class ConversationServiceImpl(
    private val conversationRepository: ConversationRepository
) : ConversationService {
    // ...
}

@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration(
    private val authManager: JwtReactiveAuthenticationManager,
    private val contextRepository: JwtSecurityContextRepository,
    private val apiAuthenticationEntryPoint: ApiAuthenticationEntryPoint,
)
```

## Bad

```kotlin
@Service
class ConversationServiceImpl : ConversationService {
    // Field injection: mutable, nullable until Spring wires it, awkward to construct in tests
    @Autowired
    lateinit var conversationRepository: ConversationRepository
}
```
