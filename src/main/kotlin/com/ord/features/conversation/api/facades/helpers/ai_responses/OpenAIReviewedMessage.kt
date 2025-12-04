package com.ord.features.conversation.api.facades.helpers.ai_responses

import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.AlternativeExpression
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.Mistake
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.VocabularyEnrichment

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

    val grammar: Int,
    val vocabulary: Int,
    val answerLength: Int,
    val naturalness: Int,
    val coherenceWithContext: Int,
    val registerAppropriate: Boolean,

    val mistakes: List<Mistake>,
    val strengthsIdentified: List<String>,

    val vocabularyEnrichment: List<VocabularyEnrichment>,
    val alternativeExpressions: List<OpenAIAlternativeExpression>,  // Uses intermediate type for note field

    val culturalNote: String  // Empty string if not applicable
) {
    /**
     * Maps OpenAI response to domain model.
     * Converts empty strings to nulls and Lists to Sets.
     */
    fun toDomain(): ReviewedUserConversationMessage {
        return ReviewedUserConversationMessage(
            sabotage = sabotage.ifEmpty { null },
            grammar = grammar,
            vocabulary = vocabulary,
            answerLength = answerLength,
            naturalness = naturalness,
            coherenceWithContext = coherenceWithContext,
            registerAppropriate = registerAppropriate,
            mistakes = mistakes.toSet(),
            strengthsIdentified = strengthsIdentified.toSet(),
            vocabularyEnrichment = vocabularyEnrichment.toSet(),
            alternativeExpressions = alternativeExpressions.map { it.toDomain() }.toSet(),
            culturalNote = culturalNote.ifEmpty { null }
        )
    }
}

/**
 * Intermediate version of AlternativeExpression for OpenAI structured outputs.
 * The `note` field is required (uses empty string for null).
 */
data class OpenAIAlternativeExpression(
    val context: String,
    val alternatives: List<String>,
    val note: String  // Empty string if not applicable
) {
    fun toDomain(): AlternativeExpression {
        return AlternativeExpression(
            context = context,
            alternatives = alternatives.toSet(),
            note = note.ifEmpty { null }
        )
    }
}
