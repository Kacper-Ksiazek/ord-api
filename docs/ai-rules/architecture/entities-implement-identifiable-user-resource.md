# User-owned entities implement `IdentifiableUserResource`

Persisted, user-scoped entities are `@Table` data classes that implement `IdentifiableUserResource`, overriding `val id: UUID? = null` (nullable so R2DBC treats null ids as inserts) and `userId: UUID`. This contract is what lets the entity plug into `UserResourceRepository` / `UserResourceService` and guarantees all data is user-scoped.

## Good

```kotlin
@Table("conversations")
data class ConversationEntity(
    @Id
    override val id: UUID? = null,

    val topic: String,
    val language: LanguageName,
    val type: ConversationType,

    override var userId: UUID,

    var createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now()
) : IdentifiableUserResource
```

## Bad

```kotlin
@Table("conversations")
data class ConversationEntity(
    @Id
    val id: UUID = UUID.randomUUID(), // non-null id breaks insert/update detection
    val topic: String,
    val ownerId: UUID,               // not the IdentifiableUserResource `userId` contract
) // does not implement IdentifiableUserResource → cannot use UserResourceRepository/Service
```
