package com.backend.ord.core.word.ai

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.backend.ord.core.word.model.enums.WordExtraMark
import com.backend.ord.core.word.model.enums.WordType
import com.backend.ord.shared.utils.EnumUtils.joinEnumValues

object PromptsWords {
    fun generateWordManualPrompt(
        word: String,
        wordLanguage: LanguageName,
        desiredLanguage: LanguageName,
        proficiency: LanguageProficiencyLevel,
        generativeContentLanguage: LanguageName,
    ): String {
        return """
                Response as a foreign language tutor. Generate a manual entry for $wordLanguage "$word" at $proficiency proficiency level in $desiredLanguage language. 
                Explain always the most common usage of the word, do not provide any rare or outdated meanings.

                Generate response in JSON format matching following typescript interface:

                type response = {
                    translation: string, // Translation of the word in $desiredLanguage translateTo. If the word is an idiom or a phrase, provide a translation that is as close as possible to the original meaning, do not translate it literally.
                    definition: string, // One or two short and concise sentences in $generativeContentLanguage
                    type: ${WordType::class.joinEnumValues(separator = " | ")},
                    extraMark: null | ${WordExtraMark::class.joinEnumValues(separator = " | ")}, // Leave null if none of the options are good enough
                    useCases: string[], // If word has multiple definitions, provide multiple use cases in $generativeContentLanguage
                    exampleSentences: {
                        sentence: string, // Sentence in $wordLanguage
                        translation: string // Sentence in $desiredLanguage
                    }[] // At least 3 examples. In both languages, the word and its translation should be surrounded with single asterisks.
                }

                Additionally, return exactly:
                    - WORD_MISSPELLED if the word is misspelled
                    - NON_EXISTENT_WORD if the word does not exist in the language
            """.trimIndent()
    }
}