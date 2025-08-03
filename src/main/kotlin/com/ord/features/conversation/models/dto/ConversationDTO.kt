package com.ord.features.conversation.models.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.user.model.UserDTO
import com.ord.features.conversation.models.enums.ConversationAIResponseLength
import com.ord.features.conversation.models.enums.ConversationGoal
import com.ord.features.conversation.models.enums.ConversationTone
import java.time.Instant
import java.util.*

data class ConversationDTO(
    val id: UUID = UUID.randomUUID(),

    val topic: String,

    val language: LanguageName,
    val proficiencyLevel: LanguageProficiencyLevel,

    val goal: ConversationGoal,
    val aiTone: ConversationTone,
    val aiResponseLength: ConversationAIResponseLength,

    val additionalContext: String,

    val user: UserDTO,
    val userId: UUID = user.id,

    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
