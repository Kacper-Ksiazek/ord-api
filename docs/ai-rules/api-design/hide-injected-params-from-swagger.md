# Hide framework-injected parameters from the API docs

Parameters resolved by the framework rather than supplied by the client — the `@AuthenticatedUser user: UserDTO` argument and the `ServerWebExchange` — must be annotated with `@Parameter(hidden = true)`. Otherwise SpringDoc exposes them as bogus query/body params in the OpenAPI spec and pollutes the generated TypeScript client.

## Good

```kotlin
fun createConversation(
    @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
    @Valid @RequestBody body: CreateConversationRequest
): Mono<ResponseEntity<ConversationDTO>> = ...

fun verifyOtp(
    @Valid @RequestBody body: OtpVerifyDto,
    @Parameter(hidden = true) exchange: ServerWebExchange
): Mono<ResponseEntity<UserDTO>> = ...
```

## Bad

```kotlin
fun createConversation(
    @AuthenticatedUser user: UserDTO,        // leaks into the OpenAPI spec as a fake parameter
    @Valid @RequestBody body: CreateConversationRequest
): Mono<ResponseEntity<ConversationDTO>> = ...
```
