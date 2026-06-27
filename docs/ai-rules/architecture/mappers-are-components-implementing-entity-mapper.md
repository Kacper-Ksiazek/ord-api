# Mappers are `@Component`s implementing the entity-mapper interfaces

Entity↔DTO conversion lives in dedicated mapper classes annotated `@Component`, implementing `UnidirectionalEntityMapper<Entity, DTO>` (override `toDTO`) — or `BidirectionalEntityMapper<Entity, DTO>` when `toEntity` is also needed. Mappers live beside the model they map and are injected into facades/repositories; never inline ad-hoc DTO construction where a mapper exists.

## Good

```kotlin
@Component
class ConversationMapper(
    private val userMapper: UserMapper,
    private val conversationMessageMapper: ConversationMessageMapper,
) : UnidirectionalEntityMapper<ConversationEntity, ConversationDTO> {
    override fun toDTO(entity: ConversationEntity): ConversationDTO {
        return ConversationDTO(
            id = entity.id ?: error("Conversation id must not be null"),
            topic = entity.topic,
            language = entity.language,
            messages = entity.messages.map { conversationMessageMapper.toDTO(it) }.toMutableList(),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }
}
```

## Bad

```kotlin
// Plain object, no @Component, not implementing the mapper interface
object ConversationMapper {
    fun map(entity: ConversationEntity) = ConversationDTO(
        id = entity.id!!,
        topic = entity.topic,
        /* ... */
    )
}
```
