package com.ord.features.conversation.services.impl

import com.ord.features.conversation.repositories.ConversationMessageRepository
import com.ord.features.conversation.services.ConversationMessageService
import org.springframework.stereotype.Service

@Service
class ConversationMessageServiceImpl(
    override val repository: ConversationMessageRepository
) : ConversationMessageService
