# Use the canonical databaseClient.sql(...).map { row -> }.all()/.one() pattern

Custom queries follow one shape: a triple-quoted SQL string with `:named` placeholders, then `template.databaseClient.sql(query)`, one `.bind("name", value)` per placeholder, a `.map { row -> ... }` that builds the target object, and a terminal `.all()` (many rows) or `.one()` (single row). Use `.fetch().all()` only when you need the raw `Map<String, Any>` rows for post-processing.

## Good

```kotlin
override fun findRecentTopics(
    userId: UUID,
    type: ConversationType,
    language: LanguageName,
    limit: Int
): Flux<String> {
    val query = """
        SELECT c.topic
        FROM conversations c
        WHERE c.user_id = :userId
            AND c.type = :type
            AND c.language = :language
        ORDER BY c.created_at DESC
        LIMIT :limit
    """

    return template.databaseClient
        .sql(query)
        .bind("userId", userId)
        .bind("type", type.name)
        .bind("language", language.name)
        .bind("limit", limit)
        .map { row -> row.get("topic", String::class.java)!! }
        .all()
}
```

## Bad

```kotlin
// String-concatenated values (SQL injection + no binding) and a manual
// connection/statement instead of the databaseClient.sql(...).map{}.all() chain.
override fun findRecentTopics(userId: UUID, /* ... */): Flux<String> {
    val query = "SELECT topic FROM conversations WHERE user_id = '$userId'"
    return template.databaseClient.sql(query).fetch().all()
        .map { it["topic"].toString() }
}
```
