package com.ord.features.conversation.services

import com.ord.features.conversation.api.facades.helpers.ai_responses.ReviewedUserConversationMessage
import com.ord.features.conversation.models.conversation_message.ConversationMessageEntity
import com.ord.features.conversation.models.conversation_user_message_feedback.ConversationUserMessageFeedbackEntity
import com.ord.features.conversation.models.conversation_message.enums.ConversationMessageSender
import reactor.core.publisher.Mono
import java.util.UUID

interface ConversationMessageService {
    fun createMessage(
        conversationId: UUID,
        sender: ConversationMessageSender,
        content: String,
        messageOrder: Int
    ): Mono<ConversationMessageEntity>


    fun createMessageWithFeedback(
        conversationId: UUID,
        messageOrder: Int,
        content: String,
        aiFeedback: ReviewedUserConversationMessage
    ): Mono<ConversationMessageEntity>
}
