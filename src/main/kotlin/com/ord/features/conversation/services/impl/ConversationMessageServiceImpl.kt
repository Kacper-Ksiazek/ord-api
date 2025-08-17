package com.ord.features.conversation.services.impl

import com.ord.features.conversation.models.dto.ConversationUserMessageFeedbackDTO
import com.ord.features.conversation.models.entities.ConversationMessageEntity
import com.ord.features.conversation.models.entities.ConversationUserMessageFeedbackEntity
import com.ord.features.conversation.models.enums.ConversationMessageSender
import com.ord.features.conversation.models.mappers.ConversationUserMessageFeedbackMapper
import com.ord.features.conversation.repositories.ConversationMessageRepository
import com.ord.features.conversation.services.ConversationMessageService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ConversationMessageServiceImpl(
    private val conversationMessageRepository: ConversationMessageRepository,
    private val conversationUserMessageFeedbackMapper: ConversationUserMessageFeedbackMapper
) : ConversationMessageService {
    override fun createMessage(
        conversationId: UUID,
        sender: ConversationMessageSender,
        content: String,
        messageOrder: Int
    ): ConversationMessageEntity {
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
        feedback: ConversationUserMessageFeedbackEntity
    ): ConversationMessageEntity {
        return conversationMessageRepository.save(
            ConversationMessageEntity(
                conversationId = conversationId,
                sender = ConversationMessageSender.USER,
                content = content,
                messageOrder = messageOrder,
                feedback = feedback
            )
        )
    }
}
