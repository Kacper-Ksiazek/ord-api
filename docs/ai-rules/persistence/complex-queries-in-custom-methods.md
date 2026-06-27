# Put complex queries in *CustomMethods + @Repository impl

When a query needs joins, aggregation, dynamic conditions, or custom projection that derived methods can't express, declare it in a `*CustomMethods` interface and compose it into the main repository interface alongside `UserResourceRepository<TEntity>`. Implement it in a `@Repository`-annotated `*Impl` class that injects `R2dbcEntityTemplate` and uses `template.databaseClient.sql(...)`.

## Good

```kotlin
interface ConversationRepositoryCustomMethods {
    fun findByIdOrFailWithMessages(id: UUID, userId: UUID): Mono<ConversationDTO>
    fun findAllWithFilters(userId: UUID, filters: ConversationListFilters): Flux<ConversationEntity>
}

interface ConversationRepository :
    UserResourceRepository<ConversationEntity>,
    ConversationRepositoryCustomMethods

@Repository
class ConversationRepositoryCustomMethodsImpl(
    private val template: R2dbcEntityTemplate
) : ConversationRepositoryCustomMethods {
    override fun findByIdOrFailWithMessages(id: UUID, userId: UUID): Mono<ConversationDTO> {
        // multi-join SQL via template.databaseClient.sql(...)
    }
}
```

## Bad

```kotlin
// Cramming a multi-join, custom-projection query into a derived-method
// name is impossible/unreadable, and putting raw SQL on the base
// ReactiveCrudRepository interface breaks the *CustomMethods convention.
interface ConversationRepository : UserResourceRepository<ConversationEntity> {
    fun findByIdJoinMessagesJoinAnalysisJoinLearningTipsAndUserId(
        id: UUID,
        userId: UUID
    ): Mono<ConversationDTO>
}
```
