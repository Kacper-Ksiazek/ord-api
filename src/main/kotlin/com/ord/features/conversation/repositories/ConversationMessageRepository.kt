package com.ord.features.conversation.repositories

import com.ord.features.conversation.models.entities.ConversationMessageEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.*

interface ConversationMessageRepository : ReactiveCrudRepository<ConversationMessageEntity, UUID>