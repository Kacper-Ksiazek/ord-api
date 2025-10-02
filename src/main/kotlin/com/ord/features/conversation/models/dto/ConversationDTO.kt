package com.ord.features.conversation.models.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.features.conversation.models.enums.ConversationType
import com.ord.features.conversation.models.enums.ConversationTone
import java.time.Instant
import java.util.*

data class ConversationDTO(
    val id: UUID,

    val topic: String,

    val language: LanguageName,
    val proficiencyLevel: LanguageProficiencyLevel,

    val type: ConversationType,
    val aiTone: ConversationTone,
    val aiInterlocutorName: String? = null,
    val aiInterlocutorAvatarId: String? = null,

    val additionalContext: String? = null,
    val messages: MutableList<ConversationMessageDTO>,

    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
