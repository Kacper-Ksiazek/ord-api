package com.ord.features.conversation.models.mappers

import com.ord.features.conversation.models.dto.ConversationUserMessageFeedbackDTO
import com.ord.features.conversation.models.entities.ConversationMessageEntity
import com.ord.features.conversation.models.entities.ConversationUserMessageFeedbackEntity
import com.ord.shared.models.mappers.UnidirectionalEntityMapper
import org.springframework.stereotype.Component

@Component
class ConversationUserMessageFeedbackMapper(
) : UnidirectionalEntityMapper<ConversationUserMessageFeedbackEntity, ConversationUserMessageFeedbackDTO> {
    override fun toDTO(entity: ConversationUserMessageFeedbackEntity): ConversationUserMessageFeedbackDTO {
        return ConversationUserMessageFeedbackDTO(
            id = entity.id,
            grammar = entity.grammar,
            vocabulary = entity.vocabulary,
            answerLength = entity.answerLength,
            suggestedAnswer = entity.suggestedAnswer,
            comment = entity.comment,
        )
    }
}
