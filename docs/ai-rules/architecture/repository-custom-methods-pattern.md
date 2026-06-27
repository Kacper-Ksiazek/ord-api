# Put custom queries behind a `CustomMethods` interface + R2DBC impl

A feature repository interface extends `UserResourceRepository<TEntity>` (for inherited user-scoped CRUD) and a `<Entity>RepositoryCustomMethods` interface for anything beyond derived queries. Hand-written queries are implemented in a `@Repository` class named `<Entity>RepositoryCustomMethodsImpl` in the `impl/` subpackage, using `R2dbcEntityTemplate.databaseClient` with named `.bind(...)` parameters. Spring Data composes the derived-query repository with this impl automatically.

## Good

```kotlin
// repositories/ConversationRepository.kt
interface ConversationRepositoryCustomMethods {
    fun findRecentTopics(userId: UUID, type: ConversationType, language: LanguageName, limit: Int): Flux<String>
}

interface ConversationRepository :
    UserResourceRepository<ConversationEntity>,
    ConversationRepositoryCustomMethods

// repositories/impl/ConversationRepositoryCustomMethodsImpl.kt
@Repository
class ConversationRepositoryCustomMethodsImpl(
    private val template: R2dbcEntityTemplate,
) : ConversationRepositoryCustomMethods {
    override fun findRecentTopics(userId: UUID, type: ConversationType, language: LanguageName, limit: Int): Flux<String> {
        return template.databaseClient
            .sql("SELECT c.topic FROM conversations c WHERE c.user_id = :userId AND c.type = :type LIMIT :limit")
            .bind("userId", userId)
            .bind("type", type.name)
            .bind("limit", limit)
            .map { row -> row.get("topic", String::class.java)!! }
            .all()
    }
}
```

## Bad

```kotlin
// Raw SQL crammed into the service layer instead of a repository custom-methods impl
@Service
class ConversationServiceImpl(private val template: R2dbcEntityTemplate) : ConversationService {
    fun recentTopics(userId: UUID) = template.databaseClient
        .sql("SELECT topic FROM conversations WHERE user_id = '$userId'") // also: string-interpolated, unbound
        .map { it.get("topic", String::class.java)!! }
        .all()
}
```
