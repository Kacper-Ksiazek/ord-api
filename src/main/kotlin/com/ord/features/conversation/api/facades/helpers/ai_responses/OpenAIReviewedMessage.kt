package com.ord.features.conversation.api.facades.helpers.ai_responses

import com.ord.features.conversation.models.conversation_user_message_feedback.enums.ConversationMessageSuggestionType
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.ConversationMessageMistake
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.ConversationMessageStrength
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.ConversationMessageSuggestion

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

    val correctedMessage: String,
    val tutorComment: String,

    val grammar: Int,
    val vocabulary: Int,
    val answerLength: Int,
    val naturalness: Int,
    val coherenceWithContext: Int,
    val registerAppropriate: Boolean,

    val mistakes: List<ConversationMessageMistake>,
    val strengths: List<ConversationMessageStrength>,

    val suggestions: List<OpenAISuggestion>
) {
    /**
     * Maps OpenAI response to domain model.
     * Converts empty strings to nulls and Lists to Sets.
     */
    fun toDomain(): ReviewedUserConversationMessage {
        return ReviewedUserConversationMessage(
            sabotage = sabotage.ifEmpty { null },
            correctedMessage = correctedMessage,
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
    val suggestionType: ConversationMessageSuggestionType,
    val alternatives: List<String>,
    val explanation: String
) {
    fun toDomain(): ConversationMessageSuggestion {
        return ConversationMessageSuggestion(
            original = original,
            suggestionType = suggestionType,
            alternatives = alternatives.toSet(),
            explanation = explanation
        )
    }
}
