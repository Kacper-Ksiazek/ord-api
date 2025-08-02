package com.ord.features.conversation.repositories

import com.ord.features.conversation.models.entities.ConversationEntity
import com.ord.shared.repositories.UserResourceRepository

interface ConversationRepository : UserResourceRepository<ConversationEntity>