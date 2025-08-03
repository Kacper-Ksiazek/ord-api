package com.ord.features.conversation.repositories

import com.ord.features.conversation.models.entities.ConversationUserMessageFeedbackEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ConversationUserMessageFeedbackRepository : JpaRepository<ConversationUserMessageFeedbackEntity, UUID>