# Use switchIfEmpty(Mono.error(...)) for not-found

When a reactive lookup may complete empty, translate emptiness into a domain error with `switchIfEmpty(Mono.error(NotFoundException(...)))`. Do not let an empty `Mono` propagate silently — downstream `map`/`flatMap` would simply be skipped and the caller would get an empty response instead of a 404.

## Good

```kotlin
fun findByIdOrFail(
    id: UUID,
    userId: UUID? = null,
    message: String = "Entity not found"
): Mono<TEntity> {
    return findById(id, userId)
        .switchIfEmpty(Mono.error(NotFoundException(message)))
}
```

## Bad

```kotlin
fun findByIdOrFail(
    id: UUID,
    userId: UUID? = null,
): Mono<TEntity> {
    // Empty Mono passes through silently: no 404, downstream map is skipped
    return findById(id, userId)
}
```
