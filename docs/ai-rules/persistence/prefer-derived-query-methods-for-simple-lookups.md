# Prefer derived query methods for simple lookups

For simple lookups, filters, and deletes, declare Spring Data R2DBC derived query methods on the repository interface instead of writing SQL. The shared `UserResourceRepository<TEntity>` already exposes the common user-scoped methods (`findAllByUserId`, `findByIdAndUserId`, `deleteByIdAndUserId`, ...), so reuse them and add new derived methods only when the property-name convention can express the query.

## Good

```kotlin
@NoRepositoryBean
interface UserResourceRepository<TEntity : Any> : ReactiveCrudRepository<TEntity, UUID> {
    fun findAllByUserId(userId: UUID): Flux<TEntity>

    fun findAllByIdInAndUserId(ids: Set<UUID>, userId: UUID): Flux<TEntity>

    fun findByIdAndUserId(id: UUID, userId: UUID): Mono<TEntity?>

    fun deleteByIdAndUserId(id: UUID, userId: UUID): Mono<Void>
}
```

## Bad

```kotlin
// Hand-written SQL for a trivial single-column filter that the
// derived-query convention already covers via findAllByUserId(userId).
override fun findAllByUserId(userId: UUID): Flux<ConversationEntity> {
    return template.databaseClient
        .sql("SELECT * FROM conversations WHERE user_id = :userId")
        .bind("userId", userId)
        .map { row -> /* manual row mapping ... */ }
        .all()
}
```
