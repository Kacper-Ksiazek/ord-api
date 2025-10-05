package com.ord.features.conversation.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.conversation.models.enums.ConversationType
import jakarta.validation.constraints.Size

data class GenerateAIInterlocutorDataRequest(
    @field:Size(min = 1, max = 500, message = "Topic must be between 1 and 500 characters")
    val topic: String,

    @field:Size(max = 1000, message = "Additional context must be less than 1000 characters")
    val additionalContext: String? = null,

    val conversationType: ConversationType,
    val language: LanguageName
)