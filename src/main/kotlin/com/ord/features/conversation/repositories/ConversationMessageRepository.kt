package com.ord.features.conversation.repositories

import com.ord.features.conversation.models.entities.ConversationMessageEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ConversationMessageRepository : JpaRepository<ConversationMessageEntity, UUID>