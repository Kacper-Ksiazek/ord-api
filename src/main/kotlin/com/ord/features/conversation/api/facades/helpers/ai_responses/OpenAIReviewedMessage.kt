package com.ord.features.conversation.api.facades.helpers.ai_responses

import com.ord.features.conversation.models.conversation_user_message_feedback.enums.SuggestionType
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.Mistake
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.Strength
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.Suggestion

/**
 * Intermediate DTO for OpenAI structured outputs response.
 *
 * This DTO matches OpenAI's structured output schema where all fields are required.
 * Nullable fields use empty string ("") to represent null values, which are then
 * mapped to proper nulls in the domain model via `toDomain()`.
 *
 * Use `toDomain()` to convert this to `ReviewedUserConversationMessage` for application use.
 */
data class OpenAIReviewedMessage(
    val sabotage: String,  // Empty string if no sabotage

    val tutorComment: String,

    val grammar: Int,
    val vocabulary: Int,
    val answerLength: Int,
    val naturalness: Int,
    val coherenceWithContext: Int,
    val registerAppropriate: Boolean,

    val mistakes: List<Mistake>,
    val strengths: List<Strength>,

    val suggestions: List<OpenAISuggestion>
) {
    /**
     * Maps OpenAI response to domain model.
     * Converts empty strings to nulls and Lists to Sets.
     */
    fun toDomain(): ReviewedUserConversationMessage {
        return ReviewedUserConversationMessage(
            sabotage = sabotage.ifEmpty { null },
            tutorComment = tutorComment,
            grammar = grammar,
            vocabulary = vocabulary,
            answerLength = answerLength,
            naturalness = naturalness,
            coherenceWithContext = coherenceWithContext,
            registerAppropriate = registerAppropriate,
            mistakes = mistakes.toSet(),
            strengths = strengths.toSet(),
            suggestions = suggestions.map { it.toDomain() }.toSet()
        )
    }
}

/**
 * Intermediate version of Suggestion for OpenAI structured outputs.
 * Uses List instead of Set for alternatives (OpenAI returns arrays).
 */
data class OpenAISuggestion(
    val original: String,
    val suggestionType: SuggestionType,
    val alternatives: List<String>,
    val explanation: String
) {
    fun toDomain(): Suggestion {
        return Suggestion(
            original = original,
            suggestionType = suggestionType,
            alternatives = alternatives.toSet(),
            explanation = explanation
        )
    }
}
