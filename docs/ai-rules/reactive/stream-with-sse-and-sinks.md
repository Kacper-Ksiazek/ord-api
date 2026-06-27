# Stream with SSE media type and Sinks

Streaming endpoints must declare `produces = [MediaType.TEXT_EVENT_STREAM_VALUE]` and return a `Flux<T>` end-to-end (no `ResponseEntity` wrapper). The producer bridges the upstream `WebClient` stream into a `Sinks.many().unicast().onBackpressureBuffer()` emitter, pushes chunks with `tryEmitNext`, and signals termination with `tryEmitComplete()` / `tryEmitError()`. Expose the sink as a cold stream via `emitter.asFlux()`.

## Good

```kotlin
// Controller: SSE media type, returns Flux<String> (no ResponseEntity)
@PostMapping("/explain-phrase", produces = [TEXT_EVENT_STREAM_VALUE])
fun explainPhrase(
    @AuthenticatedUser user: UserDTO,
    @Valid @RequestBody body: ExplainPhraseRequest
) = aiExplainerFacade.explainPhrase(body, user)

// Producer: bridge WebClient -> Sinks -> Flux
val emitter: Emitter = Sinks.many().unicast().onBackpressureBuffer<String>()
webClient.post()
    .uri(openAIProperties.apiUrl)
    .accept(MediaType.TEXT_EVENT_STREAM)
    .retrieve()
    .bodyToFlux(String::class.java)
    .doOnNext { chunk -> emitter.tryEmitNext(chunk) }
    .doOnError { error -> emitter.tryEmitError(error) }
    .doOnComplete { emitter.tryEmitComplete() }
    .subscribe()

return emitter.asFlux()
```

## Bad

```kotlin
// Buffers the whole stream into one blob and returns JSON, defeating SSE
@PostMapping("/explain-phrase") // missing TEXT_EVENT_STREAM_VALUE
fun explainPhrase(
    @AuthenticatedUser user: UserDTO,
    @Valid @RequestBody body: ExplainPhraseRequest
): Mono<ResponseEntity<String>> =
    aiExplainerFacade.explainPhrase(body, user)
        .collectList()
        .map { ResponseEntity.ok(it.joinToString("")) }
```
