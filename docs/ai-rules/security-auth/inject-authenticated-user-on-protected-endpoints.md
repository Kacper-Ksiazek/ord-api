# Inject the authenticated user via @AuthenticatedUser

Every protected endpoint identifies the caller by declaring a hidden `@Parameter(hidden = true) @AuthenticatedUser user: UserDTO` parameter and using `user.id`. The `AuthenticatedUserArgumentResolver` resolves it from the security context, so handlers must never trust a user id taken from the request body, query string, or path.

## Good

```kotlin
@PostMapping
fun createLanguageProficiency(
    @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
    @Valid @RequestBody body: CreateLanguageProficiencyRequest
): Mono<ResponseEntity<LanguageProficiencyDTO>> =
    languageProficiencyFacade.createLanguageProficiency(user, body)
```

## Bad

```kotlin
@PostMapping
fun createLanguageProficiency(
    // Caller-supplied userId is spoofable; never identify the user from the request payload
    @RequestParam userId: UUID,
    @Valid @RequestBody body: CreateLanguageProficiencyRequest
): Mono<ResponseEntity<LanguageProficiencyDTO>> =
    languageProficiencyFacade.createLanguageProficiency(userId, body)
```
