package com.ord.features.conversation.models.ai_message_tips.jsonb

/**
 * Represents a cultural context learning annotation from an AI message.
 * @property phrase Direct quote from AI message (in target language)
 * @property culturalContext Explanation of cultural significance (in generativeContentLanguage)
 * @property regionalNote Regional variations if applicable (in generativeContentLanguage)
 */
data class AnnotatedCulturalTip(
    val phrase: String,
    val culturalContext: String,
    val regionalNote: String  // Empty string if not applicable
)
