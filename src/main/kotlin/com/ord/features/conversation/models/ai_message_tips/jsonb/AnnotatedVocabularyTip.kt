package com.ord.features.conversation.models.ai_message_tips.jsonb

/**
 * Represents a vocabulary-focused learning annotation from an AI message.
 * @property word Direct quote from AI message - can be single word or phrase (in target language)
 * @property definition Clear definition (in generativeContentLanguage)
 * @property usageNote When/how to use this word (in generativeContentLanguage)
 * @property proficiencyLevel Level indicator (e.g., "B2", "C1") to help users know complexity
 */
data class AnnotatedVocabularyTip(
    val word: String,
    val definition: String,
    val usageNote: String,
    val proficiencyLevel: String
)
