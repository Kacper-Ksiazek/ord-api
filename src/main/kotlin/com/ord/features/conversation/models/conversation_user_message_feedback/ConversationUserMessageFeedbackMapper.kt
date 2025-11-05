package com.ord.features.conversation.models.conversation_user_message_feedback

import com.ord.shared.models.mappers.BidirectionalEntityMapper
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
            naturalness = entity.naturalness,
            coherenceWithContext = entity.coherenceWithContext,
            registerAppropriate = entity.registerAppropriate,
            mistakes = entity.mistakes,
            strengthsIdentified = entity.strengthsIdentified,
            vocabularyEnrichment = entity.vocabularyEnrichment,
            alternativeExpressions = entity.alternativeExpressions,
            culturalNote = entity.culturalNote,
            messageId = entity.messageId,
        )
    }

    override fun toEntity(dto: ConversationUserMessageFeedbackDTO): ConversationUserMessageFeedbackEntity {
        return ConversationUserMessageFeedbackEntity(
            id = dto.id,
            grammar = dto.grammar,
            vocabulary = dto.vocabulary,
            answerLength = dto.answerLength,
            naturalness = dto.naturalness,
            coherenceWithContext = dto.coherenceWithContext,
            registerAppropriate = dto.registerAppropriate,
            mistakes = dto.mistakes,
            strengthsIdentified = dto.strengthsIdentified,
            vocabularyEnrichment = dto.vocabularyEnrichment,
            alternativeExpressions = dto.alternativeExpressions,
            culturalNote = dto.culturalNote,
            messageId = dto.messageId,
        )
    }
}