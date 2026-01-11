package com.ord.features.conversation.models.ai_message_tips.jsonb

import com.ord.features.conversation.models.ai_message_tips.enums.PhraseType
import com.ord.features.conversation.models.ai_message_tips.enums.TipRegister

/**
 * Represents a phrase-focused learning annotation from an AI message.
 * @property phrase Direct quote from AI message (in target language)
 * @property phraseType Type of phrase: IDIOMATIC (true idioms) or LITERAL (collocations, expressions, compounds)
 * @property meaning Explanation of what the phrase means (in generativeContentLanguage)
 * @property exampleSentences Usage examples in different contexts (in target language)
 * @property register Formality level (FORMAL, INFORMAL, COLLOQUIAL)
 */
data class AnnotatedPhraseTip(
    val phrase: String,
    val phraseType: PhraseType,
    val meaning: String,
    val exampleSentences: List<String>,
    val register: TipRegister
)
