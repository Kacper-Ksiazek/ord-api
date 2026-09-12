# Persist userId on every user-owned entity

Entities that belong to a user must implement `IdentifiableUserResource`, exposing a non-null `userId: UUID` alongside the nullable `id`. This is what makes the user-scoped repository queries (`findByIdAndUserId`, `findAllByUserId`, ...) possible, so always set `userId` from the authenticated `user.id` when constructing the entity — never leave ownership implicit.

## Good

```kotlin
@Table("banks")
data class BankEntity(
    @Id
    override val id: UUID? = null,

    val name: String,
    val description: String,

    override val userId: UUID,
    var groupId: UUID? = null,

    val createdAt: Instant = Instant.now(),
) : IdentifiableUserResource
```

## Bad

```kotlin
@Table("banks")
data class BankEntity(
    @Id
    var id: UUID? = null,
    var name: String,
    var description: String,
    // No userId and no IdentifiableUserResource: rows cannot be scoped to an owner
)
```
