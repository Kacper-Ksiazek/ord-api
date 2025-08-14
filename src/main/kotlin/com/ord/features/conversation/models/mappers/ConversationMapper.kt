package com.ord.features.conversation.models.mappers

import com.ord.core.user.model.UserMapper
import com.ord.features.conversation.models.dto.ConversationDTO
import com.ord.features.conversation.models.entities.ConversationEntity
import com.ord.shared.models.mappers.UnidirectionalEntityMapper
import org.springframework.stereotype.Component

@Component
class ConversationMapper(
    private val userMapper: UserMapper,
    private val conversationMessageMapper: ConversationMessageMapper,
) : UnidirectionalEntityMapper<ConversationEntity, ConversationDTO> {
    override fun toDTO(entity: ConversationEntity): ConversationDTO {
        return ConversationDTO(
            id = entity.id,
            topic = entity.topic,
            language = entity.language,
            proficiencyLevel = entity.proficiencyLevel,
            goal = entity.goal,
            aiTone = entity.aiTone,
            aiResponseLength = entity.aiResponseLength,
            additionalContext = entity.additionalContext,

            messages = entity.messages.map { conversationMessageMapper.toDTO(it) }.toMutableList(),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
