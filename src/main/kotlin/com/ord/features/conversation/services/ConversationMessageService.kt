package com.ord.features.conversation.services

import com.ord.features.conversation.models.entities.ConversationMessageEntity
import com.ord.features.conversation.models.enums.ConversationMessageSender
import java.util.UUID

interface ConversationMessageService {
    fun createMessage(
        conversationId: UUID,
        sender: ConversationMessageSender,
        content: String,
        messageOrder: Int
    ): ConversationMessageEntity
}
