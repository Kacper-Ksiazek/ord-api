package com.ord.features.conversation.services.impl

import com.ord.features.conversation.api.facades.helpers.ai_responses.ReviewedUserConversationMessage
import com.ord.features.conversation.models.conversation_message.ConversationMessageEntity
import com.ord.features.conversation.models.conversation_user_message_feedback.ConversationUserMessageFeedbackEntity
import com.ord.features.conversation.models.conversation_message.enums.ConversationMessageSender
import com.ord.features.conversation.models.conversation_user_message_feedback.ConversationUserMessageFeedbackMapper
import com.ord.features.conversation.repositories.ConversationMessageRepository
import com.ord.features.conversation.repositories.ConversationUserMessageFeedbackRepository
import com.ord.features.conversation.services.ConversationMessageService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class ConversationMessageServiceImpl(
    private val conversationMessageRepository: ConversationMessageRepository,
    private val conversationUserMessageFeedbackRepository: ConversationUserMessageFeedbackRepository,
) : ConversationMessageService {
    override fun createMessage(
        conversationId: UUID,
        sender: ConversationMessageSender,
        content: String,
        messageOrder: Int
    ): Mono<ConversationMessageEntity> {
        return conversationMessageRepository.save(
            ConversationMessageEntity(
                conversationId = conversationId,
                sender = sender,
                content = content,
                messageOrder = messageOrder
            )
        )
    }

    override fun createMessageWithFeedback(
        conversationId: UUID,
        messageOrder: Int,
        content: String,
        aiFeedback: ReviewedUserConversationMessage
    ): Mono<ConversationMessageEntity> {
        return conversationMessageRepository
            .save(
                ConversationMessageEntity(
                    conversationId = conversationId,
                    sender = ConversationMessageSender.USER,
                    content = content,
                    messageOrder = messageOrder,
                )
            )
            .flatMap { message ->
                conversationUserMessageFeedbackRepository
                    .save(
                        ConversationUserMessageFeedbackEntity(
                            grammar = aiFeedback.grammar,
                            vocabulary = aiFeedback.vocabulary,
                            answerLength = aiFeedback.answerLength,
                            naturalness = aiFeedback.naturalness,
                            coherenceWithContext = aiFeedback.coherenceWithContext,
                            registerAppropriate = aiFeedback.registerAppropriate,
                            mistakes = aiFeedback.mistakes,
                            strengthsIdentified = aiFeedback.strengthsIdentified,
                            vocabularyEnrichment = aiFeedback.vocabularyEnrichment,
                            alternativeExpressions = aiFeedback.alternativeExpressions,
                            culturalNote = aiFeedback.culturalNote,
                            messageId = message.id!!,
                        )
                    )
                    .map { _ -> message }
            }
    }
}
