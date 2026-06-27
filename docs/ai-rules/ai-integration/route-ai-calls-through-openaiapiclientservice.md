# Route every AI call through OpenAIAPIClientService

All communication with OpenAI MUST go through `OpenAIAPIClientService` (`makeRequest`, `openSimpleStringStream`, `openStructuredArrayStream`). Never inject `WebClient`, build `OpenAIRequest`, hit `openAIProperties.apiUrl`, or talk to the provider directly from a facade, service, or controller. Only `OpenAIAPIClientServiceImpl` knows the wire format, retries, deserialization, and token logging — bypassing it loses all of that.

## Good

```kotlin
@Component
class WordAIFacadeImpl(
    private val openAIAPIClientService: OpenAIAPIClientService,
    // ...
) : WordAIFacade {
    override fun generateWordManual(body: GenerateWordManualRequest, user: UserDTO): Mono<AIGeneratedWordManual> {
        val prompt = Prompt(variant = AvailablePrompts.WORDS_GENERATE_MANUAL, params = /* ... */)

        return openAIAPIClientService
            .makeRequest(
                aiResponseType = object : TypeReference<OpenAIGeneratedWordManual>() {},
                prompt = prompt,
                userId = user.id,
                gptTokensUsageLogKey = GptTokensUsageOperationType.Words.GENERATE_MANUAL,
            )
            .map { it.toDomain(body.word) }
    }
}
```

## Bad

```kotlin
@Component
class WordAIFacadeImpl(
    private val webClient: WebClient, // never call OpenAI directly from a feature
) : WordAIFacade {
    override fun generateWordManual(body: GenerateWordManualRequest, user: UserDTO): Mono<AIGeneratedWordManual> {
        return webClient.post()
            .uri("https://api.openai.com/v1/responses")
            .bodyValue(mapOf("model" to "gpt-5-nano", "input" to "..."))
            .retrieve()
            .bodyToMono(AIGeneratedWordManual::class.java)
        // No retries, no deserialization handling, no token usage logged.
    }
}
```
