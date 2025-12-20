package com.ord.shared.prompts.structured_outputs.features.words

import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.core.word.models.word_details.enums.WordCollocationFrequency
import com.ord.core.word.models.word_details.enums.WordGender
import com.ord.shared.prompts.structured_outputs.base.StructuredOutputTemplate

val generatedWordManualSchema = StructuredOutputTemplate(
    name = "word_manual",
    schema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "word" to mapOf(
                "type" to "string",
                "description" to "The actual word this manual describes (corrected spelling). Usually same as input, but if input was misspelled, this is the corrected version."
            ),
            "translation" to mapOf(
                "type" to "string",
                "description" to "Translation of the word in the user's native language"
            ),
            "definition" to mapOf(
                "type" to "string",
                "description" to "Clear definition of the word in the target language"
            ),
            "type" to mapOf(
                "type" to "string",
                "enum" to WordType.entries,
                "description" to "Type of word or expression"
            ),
            "extraMark" to mapOf(
                "type" to "string",
                "enum" to WordExtraMark.entries,
                "description" to "Extra marks or tags for word classification by register or domain. Null if not applicable."
            ),
            "useCases" to mapOf(
                "type" to "array",
                "items" to mapOf("type" to "string"),
                "description" to "List of contexts or situations where this word is commonly used"
            ),
            "exampleSentences" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "context" to mapOf(
                            "type" to "string",
                            "description" to "Optional context for the sentence"
                        ),
                        "sentence" to mapOf(
                            "type" to "string",
                            "description" to "Example sentence using the word"
                        ),
                        "translation" to mapOf(
                            "type" to "string",
                            "description" to "Translation of the example sentence"
                        )
                    ),
                    "required" to listOf("context", "sentence", "translation"),
                    "additionalProperties" to false
                ),
                "description" to "Example sentences demonstrating word usage"
            ),
            "collocations" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "phrase" to mapOf(
                            "type" to "string",
                            "description" to "Common phrase or collocation"
                        ),
                        "translation" to mapOf(
                            "type" to "string",
                            "description" to "Translation of the phrase"
                        ),
                        "frequency" to mapOf(
                            "type" to "string",
                            "enum" to WordCollocationFrequency.entries,
                            "description" to "How frequently this collocation is used"
                        )
                    ),
                    "required" to listOf("phrase", "translation", "frequency"),
                    "additionalProperties" to false
                ),
                "description" to "Common phrases and collocations using this word"
            ),
            "pronunciation" to mapOf(
                "type" to "object",
                "properties" to mapOf(
                    "ipa" to mapOf(
                        "type" to "string",
                        "description" to "IPA (International Phonetic Alphabet) transcription"
                    ),
                    "syllables" to mapOf(
                        "type" to "string",
                        "description" to "Syllable breakdown"
                    ),
                    "stress" to mapOf(
                        "type" to "integer",
                        "minimum" to 1,
                        "maximum" to 20,
                        "description" to "Position of primary stress (syllable number)"
                    )
                ),
                "required" to listOf("ipa", "syllables", "stress"),
                "additionalProperties" to false,
                "description" to "Pronunciation information. Null if not applicable."
            ),
            "grammar" to mapOf(
                "type" to "object",
                "properties" to mapOf(
                    "gender" to mapOf(
                        "type" to "string",
                        "enum" to WordGender.entries,
                        "description" to "Grammatical gender (for languages that have it)"
                    ),
                    "definiteArticle" to mapOf(
                        "type" to "string",
                        "description" to "Definite article used with this word"
                    ),
                    "pluralForm" to mapOf(
                        "type" to "string",
                        "description" to "Plural form of the word"
                    ),
                    "comparativeForm" to mapOf(
                        "type" to "string",
                        "description" to "Comparative form for adjectives/adverbs"
                    ),
                    "superlativeForm" to mapOf(
                        "type" to "string",
                        "description" to "Superlative form for adjectives/adverbs"
                    ),
                    "irregularForms" to mapOf(
                        "type" to "object",
                        "description" to "Map of irregular forms",
                        "additionalProperties" to mapOf("type" to "string"),
                        "properties" to mapOf<String, Any>()
                    ),
                    "conjugations" to mapOf(
                        "type" to "array",
                        "items" to mapOf(
                            "type" to "object",
                            "properties" to mapOf(
                                "tense" to mapOf(
                                    "type" to "string",
                                    "description" to "Name of the tense"
                                ),
                                "forms" to mapOf(
                                    "type" to "object",
                                    "description" to "Map of person/number to conjugated form",
                                    "additionalProperties" to mapOf("type" to "string"),
                                    "properties" to mapOf<String, Any>()
                                )
                            ),
                            "required" to listOf("tense", "forms"),
                            "additionalProperties" to false
                        ),
                        "description" to "Verb conjugations for different tenses"
                    )
                ),
                "required" to listOf(
                    "gender",
                    "definiteArticle",
                    "pluralForm",
                    "comparativeForm",
                    "superlativeForm",
                    "irregularForms",
                    "conjugations"
                ),
                "additionalProperties" to false,
                "description" to "Grammar information. Null if not applicable."
            ),
            "synonyms" to mapOf(
                "type" to "array",
                "items" to mapOf("type" to "string"),
                "description" to "List of synonyms"
            ),
            "antonyms" to mapOf(
                "type" to "array",
                "items" to mapOf("type" to "string"),
                "description" to "List of antonyms"
            ),
            "commonMistakes" to mapOf(
                "type" to "array",
                "items" to mapOf("type" to "string"),
                "description" to "Common mistakes learners make with this word"
            ),
            "culturalNotes" to mapOf(
                "type" to "string",
                "description" to "Cultural context or usage notes. Null if not applicable."
            ),
            "learningTips" to mapOf(
                "type" to "string",
                "description" to "Tips for learning or remembering this word. Null if not applicable."
            )
        ),
        "required" to listOf(
            "word",
            "translation",
            "definition",
            "type",
            "extraMark",
            "useCases",
            "exampleSentences",
            "collocations",
            "pronunciation",
            "grammar",
            "synonyms",
            "antonyms",
            "commonMistakes",
            "culturalNotes",
            "learningTips"
        ),
        "additionalProperties" to false
    )
)
