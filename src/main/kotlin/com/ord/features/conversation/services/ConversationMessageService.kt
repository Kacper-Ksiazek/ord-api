package com.ord.features.conversation.services

import com.ord.features.conversation.api.facades.helpers.ai_responses.AIMessageLearningTips
import com.ord.features.conversation.api.facades.helpers.ai_responses.ConversationUserMessageAnalysisPayload
import com.ord.features.conversation.models.conversation_activity.DailyActivityCount
import com.ord.features.conversation.models.conversation_message.ConversationMessageEntity
import com.ord.features.conversation.models.conversation_message.enums.ConversationMessageSender
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

interface ConversationMessageService {
    fun createMessage(
        conversationId: UUID,
        sender: ConversationMessageSender,
        content: String,
        messageOrder: Int
    ): Mono<ConversationMessageEntity>


    fun createMessageWithAnalysis(
        conversationId: UUID,
        messageOrder: Int,
        content: String,
        aiAnalysis: ConversationUserMessageAnalysisPayload
    ): Mono<ConversationMessageEntity>

    fun saveUserMessageWithId(
        messageId: UUID,
        conversationId: UUID,
        content: String,
        messageOrder: Int
    ): Mono<ConversationMessageEntity>

    fun saveAnalysisForExistingMessage(
        messageId: UUID,
        aiAnalysis: ConversationUserMessageAnalysisPayload
    ): Mono<Void>

    fun saveLearningTipsForExistingMessage(
        messageId: UUID,
        learningTips: AIMessageLearningTips
    ): Mono<ConversationMessageEntity>

    fun countDailyMessages(userId: UUID, from: Instant, to: Instant): Flux<DailyActivityCount>
}
