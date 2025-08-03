package com.ord.features.conversation.models.mappers

import com.ord.features.conversation.models.dto.ConversationMessageDTO
import com.ord.features.conversation.models.entities.ConversationMessageEntity
import com.ord.shared.models.mappers.MapperBase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ConversationMessageMapper : MapperBase<ConversationMessageEntity, ConversationMessageDTO> {
    @Autowired
    private lateinit var conversationUserMessageFeedbackMapper: ConversationUserMessageFeedbackMapper

    override fun toEntity(dto: ConversationMessageDTO): ConversationMessageEntity {
        val messageEntity = ConversationMessageEntity(
            id = dto.id,
            messageOrder = dto.messageOrder,
            sender = dto.sender,
            content = dto.content,
            conversationId = dto.conversationId,
            feedback = null
        )

        dto.feedback?.let { feedbackDto ->
            val feedbackEntity = conversationUserMessageFeedbackMapper.toEntity(
                dto = feedbackDto,
                messageEntity = messageEntity
            )
            messageEntity.feedback = feedbackEntity
        }

        return messageEntity
    }

    override fun toDTO(entity: ConversationMessageEntity): ConversationMessageDTO {
        return ConversationMessageDTO(
            id = entity.id,
            messageOrder = entity.messageOrder,
            sender = entity.sender,
            content = entity.content,
            conversationId = entity.conversationId,
            feedback = entity.feedback?.let { conversationUserMessageFeedbackMapper.toDTO(it) },
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
