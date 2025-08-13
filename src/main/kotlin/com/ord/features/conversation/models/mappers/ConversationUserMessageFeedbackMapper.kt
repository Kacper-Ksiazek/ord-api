package com.ord.features.conversation.models.mappers

import com.ord.features.conversation.models.dto.ConversationUserMessageFeedbackDTO
import com.ord.features.conversation.models.entities.ConversationMessageEntity
import com.ord.features.conversation.models.entities.ConversationUserMessageFeedbackEntity
import org.springframework.stereotype.Component

@Component
class ConversationUserMessageFeedbackMapper {
    fun toEntity(
        dto: ConversationUserMessageFeedbackDTO,
        messageEntity: ConversationMessageEntity
    ): ConversationUserMessageFeedbackEntity {
        return ConversationUserMessageFeedbackEntity(
            id = dto.id,
            grammar = dto.grammar,
            vocabulary = dto.vocabulary,
            answerLength = dto.answerLength,
            suggestedAnswer = dto.suggestedAnswer,
            comment = dto.comment,
            message = messageEntity,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    fun toDTO(entity: ConversationUserMessageFeedbackEntity): ConversationUserMessageFeedbackDTO {
        return ConversationUserMessageFeedbackDTO(
            id = entity.id,
            grammar = entity.grammar,
            vocabulary = entity.vocabulary,
            answerLength = entity.answerLength,
            suggestedAnswer = entity.suggestedAnswer,
            comment = entity.comment,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
