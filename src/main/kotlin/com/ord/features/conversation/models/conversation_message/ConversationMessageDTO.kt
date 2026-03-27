package com.ord.features.conversation.models.conversation_message

import com.ord.features.conversation.models.conversation_ai_message_learning_tips.ConversationAIMessageLearningTipsDTO
import com.ord.features.conversation.models.conversation_user_message_analysis.ConversationUserMessageAnalysisDTO
import com.ord.features.conversation.models.conversation_message.enums.ConversationMessageSender
import java.time.Instant
import java.util.UUID

data class ConversationMessageDTO(
    val id: UUID,

    val messageOrder: Int,
    val sender: ConversationMessageSender,
    val content: String,

    val analysis: ConversationUserMessageAnalysisDTO? = null,
    val learningTips: ConversationAIMessageLearningTipsDTO? = null,

    val createdAt: Instant = Instant.now()
)