package com.ord.features.conversation.models.mappers

import com.ord.features.conversation.models.dto.ConversationMessageDTO
import com.ord.features.conversation.models.entities.ConversationMessageEntity
import com.ord.shared.models.mappers.UnidirectionalEntityMapper
import org.springframework.stereotype.Component

@Component
class ConversationMessageMapper(
    private val conversationUserMessageFeedbackMapper: ConversationUserMessageFeedbackMapper
) : UnidirectionalEntityMapper<ConversationMessageEntity, ConversationMessageDTO> {

    override fun toDTO(entity: ConversationMessageEntity): ConversationMessageDTO {
        return ConversationMessageDTO(
            id = entity.id ?: error("Conversation id must not be null"),
            messageOrder = entity.messageOrder,
            sender = entity.sender,
            content = entity.content,
            feedback = entity.feedback?.let { conversationUserMessageFeedbackMapper.toDTO(it) },
            createdAt = entity.createdAt,
        )
    }
}
