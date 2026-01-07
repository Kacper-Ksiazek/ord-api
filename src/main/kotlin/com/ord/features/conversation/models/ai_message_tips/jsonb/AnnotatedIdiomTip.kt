package com.ord.features.conversation.models.ai_message_tips.jsonb

/**
 * Represents an idiomatic expression learning annotation from an AI message.
 * @property phrase Direct quote from AI message (in target language)
 * @property meaning Literal and figurative meaning (in generativeContentLanguage)
 * @property exampleSentences Usage examples in different contexts (in target language)
 */
data class AnnotatedIdiomTip(
    val phrase: String,
    val meaning: String,
    val exampleSentences: List<String> = emptyList()
)
