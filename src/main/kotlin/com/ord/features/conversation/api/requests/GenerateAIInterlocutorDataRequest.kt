package com.ord.features.conversation.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.validators.annotations.ValidLanguageName
import com.ord.features.conversation.api.requests.dto.RecentInterlocutorInfo
import com.ord.features.conversation.models.conversation.enums.ConversationType
import com.ord.features.conversation.validators.annotations.ValidRecentInterlocutors
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class GenerateAIInterlocutorDataRequest(
    @field:Size(min = 1, max = 500, message = "Topic must be between 1 and 500 characters")
    val topic: String,

    @field:Size(max = 1000, message = "Additional context must be less than 1000 characters")
    val additionalContext: String? = null,

    val conversationType: ConversationType,

    @field:NotNull(message = "Language cannot be null")
    @field:ValidLanguageName
    val language: LanguageName,

    @field:Valid
    @field:ValidRecentInterlocutors
    val recentInterlocutors: List<RecentInterlocutorInfo>? = null
)