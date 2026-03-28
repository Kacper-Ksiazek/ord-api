package com.ord.features.conversation.models.conversation_message

import com.ord.features.conversation.models.conversation_message.enums.ConversationMessageSender
import com.ord.features.conversation.models.conversation_user_message_analysis.ConversationUserMessageAnalysisMapper
import com.ord.shared.models.mappers.UnidirectionalEntityMapper
import org.springframework.stereotype.Component

@Component
class ConversationMessageMapper(
    private val conversationUserMessageAnalysisMapper: ConversationUserMessageAnalysisMapper
) : UnidirectionalEntityMapper<ConversationMessageEntity, ConversationMessageDTO> {

    override fun toDTO(entity: ConversationMessageEntity): ConversationMessageDTO {
        return when (entity.sender) {
            ConversationMessageSender.USER -> ConversationUserMessageDTO(
                id = entity.id ?: error("Conversation message id must not be null"),
                messageOrder = entity.messageOrder,
                content = entity.content,
                createdAt = entity.createdAt,
            )
            ConversationMessageSender.AI -> ConversationAIMessageDTO(
                id = entity.id ?: error("Conversation message id must not be null"),
                messageOrder = entity.messageOrder,
                content = entity.content,
                createdAt = entity.createdAt,
            )
        }
    }
}