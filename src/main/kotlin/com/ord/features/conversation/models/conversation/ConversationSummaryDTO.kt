package com.ord.features.conversation.models.conversation

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.features.conversation.models.conversation.enums.ConversationTone
import com.ord.features.conversation.models.conversation.enums.ConversationType
import com.ord.features.conversation.models.conversation.enums.RecencyBucket
import java.time.Instant
import java.util.UUID

data class ConversationSummaryDTO(
    val id: UUID,

    val topic: String,

    val language: LanguageName,
    val proficiencyLevel: LanguageProficiencyLevel,

    val type: ConversationType,
    val aiTone: ConversationTone,
    val aiInterlocutorName: String,
    val aiInterlocutorAvatarId: String,

    val additionalContext: String? = null,

    val createdAt: Instant,
    val updatedAt: Instant,
    val recencyBucket: RecencyBucket
)
