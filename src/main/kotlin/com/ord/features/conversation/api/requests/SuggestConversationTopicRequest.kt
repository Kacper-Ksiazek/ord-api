package com.ord.features.conversation.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.features.conversation.models.enums.ConversationGoal
import jakarta.validation.constraints.Size

data class SuggestConversationTopicRequest(
    @field:Size(min = 1, max = 255, message = "Definition must be between 1 and 255 characters")
    val clueFromUser: String? = null,

    val conversationGoal: ConversationGoal,
    val language: LanguageName
)
