package com.ord.features.conversation.models.mappers

import com.ord.features.conversation.models.dto.ConversationUserMessageFeedbackDTO
import com.ord.features.conversation.models.entities.ConversationMessageEntity
import com.ord.features.conversation.models.entities.ConversationUserMessageFeedbackEntity
import com.ord.shared.models.mappers.BidirectionalEntityMapper
import com.ord.shared.models.mappers.UnidirectionalEntityMapper
import org.springframework.stereotype.Component

@Component
class ConversationUserMessageFeedbackMapper(
) : BidirectionalEntityMapper<ConversationUserMessageFeedbackEntity, ConversationUserMessageFeedbackDTO> {
    override fun toDTO(entity: ConversationUserMessageFeedbackEntity): ConversationUserMessageFeedbackDTO {
        return ConversationUserMessageFeedbackDTO(
            id = entity.id ?: error("ConversationUserMessageFeedback id must not be null"),
            grammar = entity.grammar,
            vocabulary = entity.vocabulary,
            answerLength = entity.answerLength,
            suggestedAnswer = entity.suggestedAnswer,
            comment = entity.comment,
            messageId = entity.messageId,
        )
    }

    override fun toEntity(dto: ConversationUserMessageFeedbackDTO): ConversationUserMessageFeedbackEntity {
        return ConversationUserMessageFeedbackEntity(
            id = dto.id,
            grammar = dto.grammar,
            vocabulary = dto.vocabulary,
            answerLength = dto.answerLength,

            suggestedAnswer = dto.suggestedAnswer,
            comment = dto.comment,
            messageId = dto.messageId,
        )
    }
}
