package com.ord.features.conversation.models.conversation

import com.ord.shared.models.mappers.UnidirectionalEntityMapper
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ConversationSummaryMapper : UnidirectionalEntityMapper<ConversationEntity, ConversationSummaryDTO> {
    override fun toDTO(entity: ConversationEntity): ConversationSummaryDTO {
        val now = Instant.now()
        return ConversationSummaryDTO(
            id = entity.id ?: error("Conversation id must not be null"),
            topic = entity.topic,
            language = entity.language,
            proficiencyLevel = entity.proficiencyLevel,
            type = entity.type,
            aiTone = entity.aiTone,
            aiInterlocutorName = entity.aiInterlocutorName,
            aiInterlocutorAvatarId = entity.aiInterlocutorAvatarId,
            additionalContext = entity.additionalContext,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            recencyBucket = computeRecencyBucket(activity = entity.updatedAt, now = now)
        )
    }
}
