# Use versioned, plural, kebab-case REST paths

Every controller is mapped under `/api/v1/<plural-resource>` via a class-level `@RequestMapping`. Resource collections are plural nouns; multi-word path segments use kebab-case (e.g. `/otp-request`, `/suggest-topics`). List/collection endpoints use a trailing slash (`@GetMapping("/")`, `@PostMapping("/")`), while item endpoints append a typed path variable.

## Good

```kotlin
@RestController
@RequestMapping("/api/v1/conversations")
class ConversationController(...) {

    @GetMapping("/")
    fun getConversations(...): Mono<ResponseEntity<List<ConversationSummaryDTO>>> = ...

    @GetMapping("/{conversationId}")
    fun getConversationById(
        @PathVariable conversationId: UUID,
    ): Mono<ResponseEntity<ConversationDTO>> = ...

    @PostMapping("/suggest-topics", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun suggestTopic(...) = ...
}
```

## Bad

```kotlin
@RestController
@RequestMapping("/conversation")          // missing /api/v1 prefix, singular resource
class ConversationController(...) {

    @GetMapping("/getAll")                // verb in path, no trailing slash for the collection
    fun getConversations(...) = ...

    @PostMapping("/suggestTopics")        // camelCase segment instead of kebab-case
    fun suggestTopic(...) = ...
}
```
