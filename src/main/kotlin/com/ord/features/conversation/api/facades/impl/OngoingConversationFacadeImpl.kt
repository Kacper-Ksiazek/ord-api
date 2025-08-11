package com.ord.features.conversation.api.facades.impl

import com.ord.features.conversation.api.facades.OngoingConversationFacade
import com.ord.features.conversation.models.dto.ConversationMessageDTO
import com.ord.features.conversation.models.enums.ConversationMessageSender
import org.springframework.stereotype.Service

@Service
class OngoingConversationFacadeImpl : OngoingConversationFacade {
    override fun initializeConversation() {
        TODO("Not yet implemented")
    }

    override fun requestAIMessage() {
        TODO("Not yet implemented")
    }

    override fun saveUserMessageAndGetFeedback() {
        TODO("Not yet implemented")
    }

    private fun ConversationMessageDTO.serialize(index: Int): String {
        return serializeMessage(index, sender, content)
    }

    private fun serializeMessage(
        index: Int,
        sender: ConversationMessageSender,
        message: String
    ): String {
        return """
        Order: $index
        - role: $sender
        - message: $message
    """.trimIndent()
    }
}