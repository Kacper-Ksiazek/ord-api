# Persist userId on every user-owned entity

Entities that belong to a user must implement `IdentifiableUserResource`, exposing a non-null `userId: UUID` alongside the nullable `id`. This is what makes the user-scoped repository queries (`findByIdAndUserId`, `findAllByUserId`, ...) possible, so always set `userId` from the authenticated `user.id` when constructing the entity — never leave ownership implicit.

## Good

```kotlin
@Table("quickly_added_words")
data class QuicklyAddedWordEntity(
    @Id
    override var id: UUID? = null,

    var word: String,
    var language: LanguageName,
    var isApproved: Boolean = false,
    var createdAt: Instant = Instant.now(),

    override val userId: UUID,
) : IdentifiableUserResource
```

## Bad

```kotlin
@Table("quickly_added_words")
data class QuicklyAddedWordEntity(
    @Id
    var id: UUID? = null,
    var word: String,
    var language: LanguageName,
    // No userId and no IdentifiableUserResource: rows cannot be scoped to an owner
)
```
