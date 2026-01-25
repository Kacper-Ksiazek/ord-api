package com.ord.shared.prompts.structured_outputs.features.conversation

import com.ord.core.word.models.word.enums.WordType
import com.ord.features.conversation.models.ai_message_tips.enums.PhraseType
import com.ord.features.conversation.models.ai_message_tips.enums.TipRegister
import com.ord.shared.prompts.structured_outputs.base.StructuredOutputTemplate

val aiMessageLearningTipsSchema = StructuredOutputTemplate(
    name = "ai_message_learning_tips",
    schema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "grammarTips" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "phrase" to mapOf(
                            "type" to "string",
                            "description" to "Exact quote from AI message (in target language)"
                        ),
                        "explanation" to mapOf(
                            "type" to "string",
                            "description" to "Grammar explanation (in generativeContentLanguage)"
                        ),
                        "grammarPoint" to mapOf(
                            "type" to "string",
                            "description" to "Specific grammar structure (e.g., 'Past Perfect', 'Subjunctive')"
                        ),
                        "exampleSentences" to mapOf(
                            "type" to "array",
                            "items" to mapOf("type" to "string"),
                            "description" to "1-2 example sentences showing the grammar point in context, with the relevant phrase/structure wrapped in ** markers (e.g., \"I **have been reading** for hours\") (in target language)"
                        ),
                        "register" to mapOf(
                            "type" to "string",
                            "enum" to TipRegister.entries,
                            "description" to "Formality level: FORMAL (business/academic), INFORMAL (everyday), COLLOQUIAL (casual/slangy)"
                        ),
                        "nativeLanguageEquivalent" to mapOf(
                            "type" to "string",
                            "description" to "Explanation of how this grammar structure is expressed in user's native language (in generativeContentLanguage). Use empty string if not applicable. For multiple equivalents, separate with / (e.g., 'option1 / option2')."
                        )
                    ),
                    "required" to listOf("phrase", "explanation", "grammarPoint", "exampleSentences", "register", "nativeLanguageEquivalent"),
                    "additionalProperties" to false
                ),
                "description" to "0-2 grammar-focused tips. Empty array if none applicable."
            ),
            "vocabularyTips" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "word" to mapOf(
                            "type" to "string",
                            "description" to "Word or phrase from AI message (in target language)"
                        ),
                        "definition" to mapOf(
                            "type" to "string",
                            "description" to "Clear definition (in generativeContentLanguage)"
                        ),
                        "usageNote" to mapOf(
                            "type" to "string",
                            "description" to "When/how to use (in generativeContentLanguage)"
                        ),
                        "wordType" to mapOf(
                            "type" to "string",
                            "enum" to WordType.entries,
                            "description" to "Type of word or expression (NOUN, VERB, ADJECTIVE, ADVERB, IDIOM, PHRASE)"
                        ),
                        "exampleSentences" to mapOf(
                            "type" to "array",
                            "items" to mapOf("type" to "string"),
                            "description" to "1-2 example sentences demonstrating word usage, with the word wrapped in ** markers (e.g., \"The **car** is red\") (in target language)"
                        ),
                        "register" to mapOf(
                            "type" to "string",
                            "enum" to TipRegister.entries,
                            "description" to "Formality level: FORMAL (business/academic), INFORMAL (everyday), COLLOQUIAL (casual/slangy)"
                        ),
                        "nativeLanguageEquivalent" to mapOf(
                            "type" to "string",
                            "description" to "Translation or equivalent in user's native language (in generativeContentLanguage). Use empty string if no direct equivalent exists. For multiple equivalents, separate with / (e.g., 'translation1 / translation2')."
                        )
                    ),
                    "required" to listOf(
                        "word",
                        "definition",
                        "usageNote",
                        "wordType",
                        "exampleSentences",
                        "register",
                        "nativeLanguageEquivalent"
                    ),
                    "additionalProperties" to false
                ),
                "description" to "0-2 vocabulary tips. Empty array if none applicable."
            ),
            "phraseTips" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "phrase" to mapOf(
                            "type" to "string",
                            "description" to "Exact phrase from AI message (in target language)"
                        ),
                        "phraseType" to mapOf(
                            "type" to "string",
                            "enum" to PhraseType.entries,
                            "description" to "Type of phrase: IDIOMATIC (true idioms like 'break the ice') or LITERAL (collocations, expressions, compounds)"
                        ),
                        "meaning" to mapOf(
                            "type" to "string",
                            "description" to "Explanation of what the phrase means (in generativeContentLanguage)"
                        ),
                        "exampleSentences" to mapOf(
                            "type" to "array",
                            "items" to mapOf("type" to "string"),
                            "description" to "Usage examples in different contexts, with the phrase wrapped in ** markers (e.g., \"Don't **cry over spilled milk**\") (in target language)"
                        ),
                        "register" to mapOf(
                            "type" to "string",
                            "enum" to TipRegister.entries,
                            "description" to "Formality level: FORMAL (business/academic), INFORMAL (everyday), COLLOQUIAL (casual/slangy)"
                        ),
                        "nativeLanguageEquivalent" to mapOf(
                            "type" to "string",
                            "description" to "Translation or equivalent in user's native language (in generativeContentLanguage). Use empty string if no direct equivalent exists. For multiple equivalents, separate with / (e.g., 'translation1 / translation2')."
                        )
                    ),
                    "required" to listOf("phrase", "phraseType", "meaning", "exampleSentences", "register", "nativeLanguageEquivalent"),
                    "additionalProperties" to false
                ),
                "description" to "0-2 phrase tips (idioms, collocations, useful expressions). Empty array if none applicable."
            )
        ),
        "required" to listOf(
            "grammarTips",
            "vocabularyTips",
            "phraseTips"
        ),
        "additionalProperties" to false
    )
)
