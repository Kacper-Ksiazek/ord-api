package com.ord.features.conversation.models.conversation

import com.ord.core.user.model.UserMapper
import com.ord.features.conversation.models.conversation_message.ConversationMessageMapper
import com.ord.shared.models.mappers.UnidirectionalEntityMapper
import org.springframework.stereotype.Component

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
            proficiencyLevel = entity.proficiencyLevel,
            type = entity.type,
            aiTone = entity.aiTone,
            aiInterlocutorName = entity.aiInterlocutorName,
            aiInterlocutorAvatarId = entity.aiInterlocutorAvatarId,
            additionalContext = entity.additionalContext,

            messages = entity.messages.map { conversationMessageMapper.toDTO(it) }.toMutableList(),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}