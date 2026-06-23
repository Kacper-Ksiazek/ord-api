# Stream AI output through the provided stream methods over SSE

For incremental output, use `openSimpleStringStream` (plain text) or `openStructuredArrayStream` (a stream of typed items) instead of building your own SSE/`Flux` plumbing. Expose these from controllers as `produces = [TEXT_EVENT_STREAM_VALUE]`. When the prompt must emit a delimited array, pass `OpenAIAPIClientService.STREAMING_CONTENT_SEPARATOR` into the prompt params so the client's `[[BREAK]]`-based item parser can split items. Token usage is logged automatically on stream completion.

## Good

```kotlin
// Controller
@PostMapping("/explain-phrase", produces = [TEXT_EVENT_STREAM_VALUE])
fun explainPhrase(@Valid @RequestBody body: ExplainPhraseRequest, @AuthInfo user: UserDTO): Flux<String> =
    aiExplainerFacade.explainPhrase(body, user)

// Facade
openAIAPIClientService.openStructuredArrayStream(
    prompt = prompt.toString(),
    streamedItemType = object : TypeReference<StreamSimpleItem>() {},
    userId = userId,
    gptTokensUsageLogKey = GptTokensUsageOperationType.Conversation.SUGGEST_TOPICS,
)
// prompt params include "separator" to OpenAIAPIClientService.STREAMING_CONTENT_SEPARATOR
```

## Bad

```kotlin
// Hand-rolled streaming: bypasses retries, separator parsing, and token logging.
return webClient.post()
    .uri(openAIProperties.apiUrl)
    .accept(MediaType.TEXT_EVENT_STREAM)
    .retrieve()
    .bodyToFlux(String::class.java)
    .map { raw -> raw.substringAfter("\"delta\":\"").substringBefore("\"") }
```
