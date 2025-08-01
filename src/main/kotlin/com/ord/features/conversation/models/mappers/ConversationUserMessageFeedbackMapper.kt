package com.ord.features.conversation.models.mappers

import com.ord.features.conversation.models.dto.ConversationUserMessageFeedbackDTO
import com.ord.features.conversation.models.entities.ConversationUserMessageFeedbackEntity
import com.ord.shared.models.mappers.MapperBase
import org.springframework.stereotype.Component

@Component
class ConversationUserMessageFeedbackMapper :
    MapperBase<ConversationUserMessageFeedbackEntity, ConversationUserMessageFeedbackDTO> {

    override fun toEntity(dto: ConversationUserMessageFeedbackDTO): ConversationUserMessageFeedbackEntity {
        return ConversationUserMessageFeedbackEntity(
            id = dto.id,
            rating = dto.rating,
            comment = dto.comment,
            correctForm = dto.correctForm,
            messageId = dto.messageId,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: ConversationUserMessageFeedbackEntity): ConversationUserMessageFeedbackDTO {
        return ConversationUserMessageFeedbackDTO(
            id = entity.id,
            rating = entity.rating,
            comment = entity.comment,
            correctForm = entity.correctForm,
            messageId = entity.messageId,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
