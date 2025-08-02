package com.ord.features.conversation.services.impl

import com.ord.features.conversation.repositories.ConversationRepository
import com.ord.features.conversation.services.ConversationService
import org.springframework.stereotype.Service

@Service
class ConversationServiceImpl(
    override val repository: ConversationRepository
) : ConversationService
