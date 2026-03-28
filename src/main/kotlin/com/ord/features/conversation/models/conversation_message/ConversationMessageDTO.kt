package com.ord.features.conversation.models.conversation_message

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.ord.features.conversation.models.conversation_ai_message_learning_tips.ConversationAIMessageLearningTipsDTO
import com.ord.features.conversation.models.conversation_message.enums.ConversationMessageSender
import com.ord.features.conversation.models.conversation_user_message_analysis.ConversationUserMessageAnalysisDTO
import java.time.Instant
import java.util.UUID

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "sender",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ConversationUserMessageDTO::class, name = "USER"),
    JsonSubTypes.Type(value = ConversationAIMessageDTO::class, name = "AI")
)
sealed interface ConversationMessageDTO {
    val id: UUID
    val messageOrder: Int
    val sender: ConversationMessageSender
    val content: String
    val createdAt: Instant
}

data class ConversationUserMessageDTO(
    override val id: UUID,
    override val messageOrder: Int,
    override val sender: ConversationMessageSender = ConversationMessageSender.USER,
    override val content: String,
    override val createdAt: Instant = Instant.now(),
    val analysis: ConversationUserMessageAnalysisDTO? = null,
) : ConversationMessageDTO

data class ConversationAIMessageDTO(
    override val id: UUID,
    override val messageOrder: Int,
    override val sender: ConversationMessageSender = ConversationMessageSender.AI,
    override val content: String,
    override val createdAt: Instant = Instant.now(),
    val learningTips: ConversationAIMessageLearningTipsDTO? = null,
) : ConversationMessageDTO
