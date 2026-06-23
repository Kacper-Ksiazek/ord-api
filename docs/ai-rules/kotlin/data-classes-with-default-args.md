# Model DTOs and entities as data classes with default arguments

Represent entities, request DTOs, and response payloads as `data class`. Give optional fields and server-managed fields (ids, timestamps, nullable extras) sensible defaults via `= null` or `= Instant.now()` so call sites only pass what they care about. Group related properties with blank lines for readability.

## Good

```kotlin
@Table("conversations")
data class ConversationEntity(
    @Id
    override val id: UUID? = null,

    val topic: String,
    val additionalContext: String? = null,

    val language: LanguageName,
    val proficiencyLevel: LanguageProficiencyLevel,

    val type: ConversationType,
    val aiTone: ConversationTone,
    val aiInterlocutorName: String,
    val aiInterlocutorAvatarId: String,

    override var userId: UUID,

    var createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now()
) : IdentifiableUserResource
```

## Bad

```kotlin
// Plain class, mutable setters, no defaults, optional fields are required anyway
class ConversationEntity {
    var id: UUID? = null
    var topic: String = ""
    var additionalContext: String = "" // should be nullable with a default
    var createdAt: Instant? = null      // forces every caller to set timestamps manually
}
```
