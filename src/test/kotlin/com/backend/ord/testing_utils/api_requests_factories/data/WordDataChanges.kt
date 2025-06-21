package com.backend.ord.testing_utils.api_requests_factories.data

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.word.model.enums.WordExtraMark
import com.backend.ord.core.word.model.enums.WordType
import com.backend.ord.core.word.model.json.ExampleSentence
import com.backend.ord.shared.utils.data_classes.Optional

data class WordDataChanges(
    val origin: Optional<String?> = Optional(null),
    val translation: Optional<String?> = Optional(null),
    val definition: Optional<String?> = Optional(null),

    val type: Optional<WordType?> = Optional(null),
    val translatedFrom: Optional<LanguageName?> = Optional(null),
    val extraMark: Optional<WordExtraMark?> = Optional(null),
    val translatedTo: Optional<LanguageName?> = Optional(null),

    val useCases: Optional<Set<String>?> = Optional(null),
    val exampleSentences: Optional<Set<ExampleSentence>?> = Optional(null)
)