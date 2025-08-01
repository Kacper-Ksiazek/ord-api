package com.ord.features.conversation.models.mappers

import com.ord.features.conversation.models.dto.ConversationMessageDTO
import com.ord.features.conversation.models.entities.ConversationMessageEntity
import com.ord.shared.models.mappers.MapperBase
import org.springframework.stereotype.Component

@Component
class ConversationMessageMapper(
    private val conversationUserMessageFeedbackMapper: ConversationUserMessageFeedbackMapper
) : MapperBase<ConversationMessageEntity, ConversationMessageDTO> {
    override fun toEntity(dto: ConversationMessageDTO): ConversationMessageEntity {
        return ConversationMessageEntity(
            id = dto.id,
            sender = dto.sender,
            content = dto.content,
            userId = dto.userId,
            conversationId = dto.conversationId,
            feedback = conversationUserMessageFeedbackMapper.toEntityOrNull(dto.feedback),
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: ConversationMessageEntity): ConversationMessageDTO {
        return ConversationMessageDTO(
            id = entity.id,
            sender = entity.sender,
            content = entity.content,
            userId = entity.userId,
            conversationId = entity.conversationId,
            feedback = conversationUserMessageFeedbackMapper.toDTOOrNull(entity.feedback),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}

