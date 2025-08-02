package com.ord.features.conversation.services.impl

import com.ord.features.conversation.repositories.ConversationUserMessageFeedbackRepository
import com.ord.features.conversation.services.ConversationUserMessageFeedbackService
import org.springframework.stereotype.Service

@Service
class ConversationUserMessageFeedbackServiceImpl(
    override val repository: ConversationUserMessageFeedbackRepository
) : ConversationUserMessageFeedbackService
