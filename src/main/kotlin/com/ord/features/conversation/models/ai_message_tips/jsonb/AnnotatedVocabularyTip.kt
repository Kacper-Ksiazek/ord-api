package com.ord.features.conversation.models.ai_message_tips.jsonb

import com.ord.core.word.models.word.enums.WordType
import com.ord.features.conversation.models.ai_message_tips.enums.TipRegister

/**
 * Represents a vocabulary-focused learning annotation from an AI message.
 * @property word Direct quote from AI message - can be single word or phrase (in target language)
 * @property definition Clear definition (in generativeContentLanguage)
 * @property usageNote When/how to use this word (in generativeContentLanguage)
 * @property wordType Type of word or expression (NOUN, VERB, ADJECTIVE, ADVERB, IDIOM, PHRASE)
 * @property exampleSentences Example sentences demonstrating word usage (in target language)
 * @property register Formality level (FORMAL, INFORMAL, COLLOQUIAL)
 * @property nativeLanguageEquivalent Translation or equivalent in user's native language (in generativeContentLanguage). Empty string if no direct equivalent exists.
 */
data class AnnotatedVocabularyTip(
    val word: String,
    val definition: String,
    val usageNote: String,
    val wordType: WordType,
    val exampleSentences: List<String>,
    val register: TipRegister,
    val nativeLanguageEquivalent: String
)
