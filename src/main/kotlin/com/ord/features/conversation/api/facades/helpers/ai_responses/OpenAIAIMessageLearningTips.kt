package com.ord.features.conversation.api.facades.helpers.ai_responses

import com.ord.features.conversation.models.ai_message_tips.jsonb.AnnotatedGrammarTip
import com.ord.features.conversation.models.ai_message_tips.jsonb.AnnotatedIdiomTip
import com.ord.features.conversation.models.ai_message_tips.jsonb.AnnotatedVocabularyTip

/**
 * Intermediate DTO for OpenAI structured outputs response.
 * This DTO matches OpenAI's structured output schema where all fields are required.
 * Empty arrays represent no tips in that category.
 *
 * Use `toDomain()` to convert this to `AIMessageLearningTips` for application use.
 */
data class OpenAIAIMessageLearningTips(
    val grammarTips: List<AnnotatedGrammarTip>,
    val vocabularyTips: List<AnnotatedVocabularyTip>,
    val idiomTips: List<AnnotatedIdiomTip>
) {
    /**
     * Maps OpenAI response to domain model.
     * Converts Lists to Sets to prevent duplicates.
     */
    fun toDomain(): AIMessageLearningTips {
        return AIMessageLearningTips(
            grammarTips = grammarTips.toSet(),
            vocabularyTips = vocabularyTips.toSet(),
            idiomTips = idiomTips.toSet()
        )
    }
}
