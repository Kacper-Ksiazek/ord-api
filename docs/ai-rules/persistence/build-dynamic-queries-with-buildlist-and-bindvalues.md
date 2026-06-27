# Build dynamic queries with buildList + bindValues

For optional/conditional filters, build the WHERE conditions with `buildList { ... }.joinToString(" AND ")` and accumulate the matching parameters into a `mutableMapOf<String, Any>(...)`. Add each placeholder to the SQL only when its filter is present, and add the corresponding entry to the bindings map under the exact same name. Pass the whole map with `.bindValues(bindings)` so the placeholder set and the bound set always stay in sync.

## Good

```kotlin
val conditions = buildList {
    add("c.user_id = :userId")
    if (filters.search != null) add("(LOWER(c.topic) LIKE :search OR LOWER(c.ai_interlocutor_name) LIKE :search)")
    if (filters.type != null) add("c.type = :type")
}.joinToString(" AND ")

val bindings = mutableMapOf<String, Any>("userId" to userId).apply {
    filters.search?.let { put("search", "%${it.lowercase()}%") }
    filters.type?.let { put("type", it.name) }
}

val query = "SELECT * FROM conversations c WHERE $conditions ORDER BY c.updated_at DESC, c.id DESC"

return template.databaseClient
    .sql(query)
    .bindValues(bindings)
    .map { row -> /* ... */ }
    .all()
```

## Bad

```kotlin
// Concatenating filter values into the SQL (injection-prone) and using
// individual .bind() calls that drift out of sync with the placeholders.
var query = "SELECT * FROM conversations WHERE user_id = '$userId'"
if (filters.type != null) query += " AND type = '${filters.type}'"
return template.databaseClient.sql(query).map { /* ... */ }.all()
```
