# Scope all data access by the authenticated user's id

Every read, update, and delete of a user-owned resource must be filtered by `userId`. Use `UserResourceService`/`UserResourceRepository` helpers such as `findByIdAndUserId`, `findAllByUserId`, and `deleteByIdAndUserId` so one user can never reach another user's rows. Never look up a user-owned entity by id alone for an authenticated request.

## Good

```kotlin
override fun deleteById(
    id: UUID,
    userId: UUID,
): Mono<Void> {
    return findByIdOrFail(id = id, userId = userId)
        .flatMap { existing ->
            repository.deleteByIdAndUserId(id = id, userId = userId)
        }
}
```

## Bad

```kotlin
fun deleteById(id: UUID): Mono<Void> {
    // No userId filter: any authenticated user can delete another user's resource
    return repository.deleteById(id)
}
```
