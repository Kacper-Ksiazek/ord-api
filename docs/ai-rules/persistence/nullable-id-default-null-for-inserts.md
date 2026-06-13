# Entities use id: UUID? = null so save() inserts

Persistable entities declare `@Id override val id: UUID? = null`. A null id tells Spring Data R2DBC the row is new and must be `INSERT`ed (the database fills the id via `gen_random_uuid()`); a non-null id means `UPDATE`. Construct new entities without setting an id, and never hardcode a client-side id for a fresh row. Use `Instant.now()` defaults for timestamps and `@Transient` for in-memory-only associations.

## Good

```kotlin
@Table("conversations")
data class ConversationEntity(
    @Id
    override val id: UUID? = null,

    val topic: String,
    // ...
    override var userId: UUID,

    var createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now()
) : IdentifiableUserResource {
    @Transient
    var messages: MutableList<ConversationMessageEntity> = mutableListOf()
}

// Creating a new row: leave id null so R2DBC performs an INSERT.
val entity = ConversationEntity(topic = "Travel", userId = userId, /* ... */)
```

## Bad

```kotlin
@Table("conversations")
data class ConversationEntity(
    @Id
    override val id: UUID = UUID.randomUUID(), // non-null id => R2DBC treats it as an UPDATE
    val topic: String,
    override var userId: UUID
) : IdentifiableUserResource
```
