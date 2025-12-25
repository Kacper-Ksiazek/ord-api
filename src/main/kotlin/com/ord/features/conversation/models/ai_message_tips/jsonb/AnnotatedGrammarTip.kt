package com.ord.features.conversation.models.ai_message_tips.jsonb

/**
 * Represents a grammar-focused learning annotation from an AI message.
 * @property phrase Direct quote from AI message (in target language)
 * @property explanation Grammar explanation (in generativeContentLanguage)
 * @property grammarPoint The specific grammar structure being highlighted (e.g., "Past Perfect", "Subjunctive")
 */
data class AnnotatedGrammarTip(
    val phrase: String,
    val explanation: String,
    val grammarPoint: String
)
